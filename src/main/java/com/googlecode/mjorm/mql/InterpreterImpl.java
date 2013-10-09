
package com.googlecode.mjorm.mql;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.query.DaoModifier;
import com.googlecode.mjorm.query.DaoQuery;
import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.googlecode.mjorm.query.criteria.Criterion;
import com.googlecode.mjorm.query.criteria.DocumentCriterion;
import com.googlecode.mjorm.query.criteria.EqualsCriterion;
import com.googlecode.mjorm.query.criteria.FieldCriterion;
import com.googlecode.mjorm.query.criteria.NotCriterion;
import com.googlecode.mjorm.query.criteria.RegexCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion.Operator;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class InterpreterImpl
	implements Interpreter {

	private static final Map<String, Object> NO_PARAMS
		= Collections.unmodifiableMap(new HashMap<String, Object>());

	private static final CommonTreeAdaptor ADAPTER = new CommonTreeAdaptor() {
		public Object create(Token payload) {
			return new CommonTree(payload);
		}
	};

	private static final Map<String, Operator> comparisonOperators
		= new HashMap<String, SimpleCriterion.Operator>();

	static {
		comparisonOperators.put(">", Operator.GT);
		comparisonOperators.put(">=", Operator.GTE);
		comparisonOperators.put("<", Operator.LT);
		comparisonOperators.put("<=", Operator.LTE);
		comparisonOperators.put("!=", Operator.NE);
		comparisonOperators.put("<>", Operator.NE);
	}

	private DB db;
	private ObjectMapper objectMapper;
	private Map<String, MqlCriterionFunction> fieldFunctions;
	private Map<String, MqlCriterionFunction> documentFunctions;
	private Map<String, MqlVariableFunction> variableFunctions;

	/**
	 * Creates it.
	 * @param db
	 * @param objectMapper
	 */
	public InterpreterImpl(DB db, ObjectMapper objectMapper) {
		this.db 				= db;
		this.objectMapper		= objectMapper;
		this.documentFunctions	= new HashMap<String, MqlCriterionFunction>();
		this.fieldFunctions		= new HashMap<String, MqlCriterionFunction>();
		this.variableFunctions	= new HashMap<String, MqlVariableFunction>();
	}

	/**
	 * Registers a field function.
	 * @param function
	 */
	public void registerFieldFunction(MqlCriterionFunction function) {
		fieldFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Registers a document function.
	 * @param function
	 */
	public void registerDocumentFunction(MqlCriterionFunction function) {
		documentFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Registers a variable function.
	 * @param function
	 */
	public void registerVariableFunction(MqlVariableFunction function) {
		variableFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Compiles the given code return the AST.
	 * @param ips
	 * @return
	 * @throws IOException
	 * @throws RecognitionException
	 */
	public CommonTree compile(InputStream ips)
		throws IOException,
		RecognitionException {

		// create the lexer and parser
		MqlLexer lexer 				= new MqlLexer(new ANTLRUpperCaseInputStream(ips));
		CommonTokenStream tokens 	= new CommonTokenStream(lexer);
		MqlParser parser 			= new MqlParser(tokens);

		// set adapter
		parser.setTreeAdaptor(ADAPTER);

		// parse
		MqlParser.start_return ast = parser.start();

		// verify
		CommonTree tree = CommonTree.class.cast(ast.getTree());
		verifyTree(tree);

		return tree;
	}

	private void verifyTree(CommonTree tree) {
		if (CommonErrorNode.class.isInstance(tree)) {
			throw new MqlException(tree);
		}
		for (int i=0; i<tree.getChildCount(); i++) {
			verifyTree(CommonTree.class.cast(tree.getChild(i)));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<InterpreterResult> interpret(Tree tree) {
		return interpret(tree, NO_PARAMS);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<InterpreterResult> interpret(Tree tree, Object... parameters) {
		Map<String, Object> params = new HashMap<String, Object>();
		for (int i=0; i<parameters.length; i++) {
			params.put(i+"", parameters[i]);
		}
		return interpret(tree, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<InterpreterResult> interpret(
		Tree tree, Map<String, Object> parameters) {
		assertTokenType(tree, MqlParser.COMMANDS);

		// prepare an execution context
		ExecutionContext ctx = new ExecutionContext();
		ctx.params = parameters;
		ctx.currentParameterIndex = 0;

		// execute
		List<InterpreterResult> ret = new ArrayList<InterpreterResult>();
		for (int i=0; i<tree.getChildCount(); i++) {
			ret.add(doInterpret(
				CommonTree.class.cast(tree.getChild(i)), ctx));
		}
		return ret;
	}


	/**
	 * Interprets a command tree.
	 * @param t
	 * @param parameters
	 * @return
	 */
	private InterpreterResult doInterpret(
		CommonTree tree, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.COMMAND);

		// setup the query
		DaoQuery query = new DaoQuery();
		query.setDB(db);
		query.setObjectMapper(objectMapper);

		// set collection
		query.setCollection(child(tree, 0).getText());

		// read criteria
		CommonTree actionTree = null;
		if (child(tree, 1).getType()==MqlParser.CRITERIA) {
			readCriteria(child(tree, 1), query, ctx);
			actionTree = child(tree, 2);
		} else {
			actionTree = child(tree, 1);
		}

		// invoke the action
		assertTokenType(actionTree, MqlParser.ACTION);
		actionTree = child(actionTree, 0);
		switch (actionTree.getType()) {

			// select
			case MqlParser.SELECT_ACTION: {
				return executeSelectAction(actionTree, query, ctx);
			}

			// explain
			case MqlParser.EXPLAIN_ACTION: {
				return executeExplainAction(actionTree, query);
			}

			// delete
			case MqlParser.DELETE_ACTION: {
				return executeDeleteAction(actionTree, query);
			}

			// update
			case MqlParser.UPDATE_ACTION: {
				return executeUpdateAction(actionTree, query, false, ctx);
			}

			// upsert
			case MqlParser.UPSERT_ACTION: {
				return executeUpdateAction(actionTree, query, true, ctx);
			}

			// find and modify
			case MqlParser.FAM_ACTION: {
				return executeFamAction(actionTree, query, ctx);
			}

			// find and delete
			case MqlParser.FAD_ACTION: {
				return executeFadAction(actionTree, query);
			}

			// zomg we're all gunna die
			default:
				throw new MqlException("Unknown action type");
		}
	}

	/**
	 * Executes a Find And Delete.
	 * @param tree
	 * @param query
	 * @return
	 */
	private InterpreterResult executeFadAction(CommonTree tree, DaoQuery query) {

		// get field list
		CommonTree fieldListTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.FIELD_LIST));
		DBObject fields = readFieldList(fieldListTree);

		// read sort
		CommonTree sortTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.SORT));
		if (sortTree!=null) {
			readSort(sortTree, query);
		}

		// execute it
		return new InterpreterResult(
			null, query.modify().findAndDelete(fields), null);
	}

	/**
	 * Executes a Find And Modify.
	 * @param tree
	 * @param query
	 * @return
	 */
	private InterpreterResult executeFamAction(CommonTree tree, DaoQuery query, ExecutionContext ctx) {

		Tree upsert = tree.getFirstChildWithType(MqlParser.UPSERT);
		Tree returnTree = tree.getFirstChildWithType(MqlParser.RETURN);
		boolean returnNew = (returnTree!=null)
			? returnTree.getChild(0).getType()==MqlParser.NEW
			: true;

		// read updateOperations
		Tree updateTree = tree.getFirstChildWithType(MqlParser.UPDATE_OPERATIONS);
		readModifiers(CommonTree.class.cast(updateTree), query, ctx);

		// get field list
		CommonTree fieldListTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.FIELD_LIST));
		DBObject fields = readFieldList(fieldListTree);

		// read sort
		CommonTree sortTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.SORT));
		if (sortTree!=null) {
			readSort(sortTree, query);
		}

		// execute it
		return new InterpreterResult(
			null, query.modify().findAndModify(returnNew, upsert!=null, fields), null);
	}
	
	/**
	 * Executes an update action.
	 * @param action
	 * @param query
	 * @param upsert
	 * @return
	 */
	private InterpreterResult executeUpdateAction(
		CommonTree tree, DaoQuery query, boolean upsert, ExecutionContext ctx) {

		// atomic? multi?
		Tree atomic = tree.getFirstChildWithType(MqlParser.ATOMIC);
		Tree multi = tree.getFirstChildWithType(MqlParser.MULTI);

		// read updateOperations
		Tree updateTree = tree.getFirstChildWithType(MqlParser.UPDATE_OPERATIONS);
		readModifiers(CommonTree.class.cast(updateTree), query, ctx);

		// execute it
		WriteResult res = query.modify()
			.setAtomic(atomic!=null)
			.update(upsert, multi!=null);

		// execute it
		return new InterpreterResult(null, null, res);
	}

	/**
	 * Executes a delete action.
	 * @param action
	 * @param query
	 * @return
	 */
	private InterpreterResult executeDeleteAction(CommonTree tree, DaoQuery query) {

		// read hint
		Tree atomic = tree.getFirstChildWithType(MqlParser.ATOMIC);

		// execute it
		WriteResult res = query
			.modify()
			.setAtomic(atomic!=null)
			.delete();

		// execute it
		return new InterpreterResult(null, null, res);
	}

	/**
	 * Executes an explain action.
	 * @param action
	 * @param query
	 * @return
	 */
	private InterpreterResult executeExplainAction(CommonTree tree, DaoQuery query) {

		// read hint
		CommonTree hintTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.HINT));
		if (hintTree!=null) {
			readHint(hintTree, query);
		}

		// execute it
		return new InterpreterResult(null, query.explain(), null);
	}

	/**
	 * Executes a select action.
	 * @param action
	 * @param query
	 * @return
	 */
	private InterpreterResult executeSelectAction(
		CommonTree tree, DaoQuery query, ExecutionContext ctx) {

		// get field list
		CommonTree fieldListTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.FIELD_LIST));
		DBObject fields = readFieldList(fieldListTree);

		// read hint
		CommonTree hintTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.HINT));
		if (hintTree!=null) {
			readHint(hintTree, query);
		}

		// read sort
		CommonTree sortTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.SORT));
		if (sortTree!=null) {
			readSort(sortTree, query);
		}

		// read limit
		CommonTree limitTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.LIMIT));
		if (limitTree!=null) {
			readLimit(limitTree, query, ctx);
		}

		// execute it
		return (fields!=null)
			? new InterpreterResult(query.findObjects(fields), null, null)
			: new InterpreterResult(query.findObjects(), null, null);
	}

	/**
	 * Reads modifiers
	 * @param tree
	 * @param query
	 */
	private void readModifiers(CommonTree tree, DaoQuery query, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.UPDATE_OPERATIONS);

		// get the modifer
		DaoModifier modifier = query.modify();

		// go through each operation
		for (int i=0; i<tree.getChildCount(); i++) {
			CommonTree modiferTree = child(tree, i);
			String field = null;
			Object value = null;

			// add the operation to the query
			switch(modiferTree.getType()) {
				case MqlParser.INC:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					assertType(value, modiferTree, Number.class);
					modifier.inc(field, Number.class.cast(value));
					break;
				case MqlParser.SET:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					modifier.set(field, value);
					break;
				case MqlParser.UNSET:
					field = child(modiferTree, 0).getText();
					modifier.unset(field);
					break;
				case MqlParser.PUSH:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					modifier.push(field, value);
					break;
				case MqlParser.PUSH_ALL:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					assertType(value, modiferTree, Object[].class);
					modifier.pushAll(field, Object[].class.cast(value));
					break;
				case MqlParser.ADD_TO_SET:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					modifier.addToSet(field, value);
					break;
				case MqlParser.ADD_TO_SET_EACH:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					assertType(value, modiferTree, Object[].class);
					modifier.addToSetEach(field, Object[].class.cast(value));
					break;
				case MqlParser.POP:
					field = child(modiferTree, 0).getText();
					modifier.pop(field);
					break;
				case MqlParser.SHIFT:
					field = child(modiferTree, 0).getText();
					modifier.pop(field);
					break;
				case MqlParser.PULL:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					modifier.pull(field, value);
					break;
				case MqlParser.PULL_ALL:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1), ctx);
					assertType(value, modiferTree, Object[].class);
					modifier.pullAll(field, Object[].class.cast(value));
					break;
				case MqlParser.RENAME:
					field = child(modiferTree, 0).getText();
					value = child(modiferTree, 1).getText();
					modifier.rename(field, String.class.cast(value));
					break;
				case MqlParser.BITWISE:
					Tree opTree = modiferTree.getChild(0);
					field = child(modiferTree, 1).getText();
					value = readVariableLiteral(child(modiferTree, 2), ctx);
					assertType(value, modiferTree, Number.class);
					if (opTree.getType()==MqlParser.AND) {
						modifier.bitwiseAnd(field, Number.class.cast(value));
					} else {
						modifier.bitwiseOr(field, Number.class.cast(value));
					}
					break;
				default:
					throw new MqlException(
						"Unknown modifier:" +modiferTree.toString());
			}
			
		}
		
	}

	
	/**
	 * Reads a sort.
	 * @param tree
	 * @param query
	 */
	private void readLimit(CommonTree tree, DaoQuery query, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.LIMIT);

		if (tree.getChildCount()==1) {
			Object num = readVariableLiteral(child(tree, 0), ctx);
			assertType(num, tree, Number.class);
			query.setMaxDocuments(Number.class.cast(num).intValue());
			
		} else {
			Object start = readVariableLiteral(child(tree, 0), ctx);
			Object num = readVariableLiteral(child(tree, 1), ctx);
			assertType(num, tree, Number.class);
			query.setFirstDocument(Number.class.cast(start).intValue());
			query.setMaxDocuments(Number.class.cast(num).intValue());
		}
	}

	/**
	 * Reads a sort.
	 * @param tree
	 * @param query
	 */
	private void readSort(CommonTree tree, DaoQuery query) {
		assertTokenType(tree, MqlParser.SORT);

		tree = CommonTree.class.cast(tree.getChild(0));
		for (int i=0; i<tree.getChildCount(); i++) {
			Tree sortField = tree.getChild(0);
			Tree direction = tree.getChild(1);
			int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
			query.addSort(sortField.getText(), dir);
		}
	}

	/**
	 * Reads a hint.
	 * @param tree
	 * @param query
	 */
	private void readHint(CommonTree tree, DaoQuery query) {
		assertTokenType(tree, MqlParser.HINT);

		// natural
		if (tree.getChild(0).getType()==MqlParser.NATURAL) {
			Tree direction = tree.getChild(1);
			int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
			query.setHint("$natural", dir);
			return;

		// string
		} else if (isString(tree.getChild(0))) {
			Tree direction = tree.getChild(1);
			int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
			query.setHint(tree.getChild(0).getText(), dir);
			return;

		// hint fields
		} else {
			DBObject hint = new BasicDBObject();
			for (int i=0; i<tree.getChildCount(); i++) {
				Tree hintField = tree.getChild(0);
				Tree direction = hintField.getChild(1);
				int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
				hint.put(hintField.getChild(0).getText(), dir);
			}
			query.setHint(hint);
		}
	}

	/**
	 * Reads a field list.
	 * @param fieldList
	 * @return
	 */
	private DBObject readFieldList(CommonTree fieldList) {
		if (fieldList==null) { return null; }
		assertTokenType(fieldList, MqlParser.FIELD_LIST);
		DBObject fields = new BasicDBObject();
		for (int i=0; i<fieldList.getChildCount(); i++) {
			if (fieldList.getChild(i).getType()==MqlParser.STAR) {
				return null;
			}
			fields.put(fieldList.getChild(i).getText(), 1);
		}
		return fields;
	}

	/**
	 * Reads criteria.
	 * @param tree
	 * @param query
	 */
	private void readCriteria(CommonTree tree, AbstractQueryCriterion<?> query, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.CRITERIA);
		for (int i=0; i<tree.getChildCount(); i++) {
			readCriterion(child(tree, i), query, ctx);
		}
	}

	/**
	 * Creates a {@link Criterion} from the given tree and
	 * adds it to the given {@link DaoQuery}.
	 * @param tree
	 * @param query
	 */
	private void readCriterion(CommonTree tree, AbstractQueryCriterion<?> query, ExecutionContext ctx) {
		DocumentCriterion criterion = null;
		String fieldName = null;
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				String functionName = child(tree, 0).getChild(0).getText().trim().toLowerCase();
				Criterion c = createCriterion(tree, ctx);
				if (!DocumentCriterion.class.isInstance(c)) {
					throw new MqlException(
						"Document function '"+functionName+"' returned a Criterion other than a DocumentCriterion");
				}
				criterion = DocumentCriterion.class.cast(c);
				break;
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				fieldName = child(tree, 0).getText().trim();
				criterion = new FieldCriterion(fieldName, createCriterion(tree, ctx));
				break;
				
			case MqlParser.COMPARE_CRITERION:
				fieldName = child(tree, 0).getText().trim();
				criterion = new FieldCriterion(fieldName, createCriterion(tree, ctx));
				break;
				
			case MqlParser.NEGATED_CRITERION:
				fieldName = child(tree, 0).getChild(0).getText().trim();
				criterion = new NotCriterion(fieldName, createCriterion(child(tree, 0), ctx));
				break;
				
			default:
				assertTokenType(tree);
		}
		query.add(criterion);
	}

	/**
	 * Creates a {@link Criterion} from the given tree.
	 * @param tree
	 * @return
	 */
	private Criterion createCriterion(CommonTree tree, ExecutionContext ctx) {
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				return readCriterionForFunctionCall(child(tree, 0), documentFunctions, ctx);
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				return readCriterionForFunctionCall(child(tree, 1), fieldFunctions, ctx);
				
			case MqlParser.COMPARE_CRITERION:
				String op = child(tree, 1).getText();
				Object value = readVariableLiteral(child(tree, 2), ctx);
				if (op.equals("=")) {
					return new EqualsCriterion(value);
				} else if (op.equals("=~")) {
					assertType(value, tree, Pattern.class);
					return new RegexCriterion(Pattern.class.cast(value));
				}
				return new SimpleCriterion(comparisonOperators.get(op), value);
				
			case MqlParser.NEGATED_CRITERION:
				Criterion c = createCriterion(child(tree, 0), ctx);
				if (!FieldCriterion.class.isInstance(c)) {
					throw new MqlException(
						"NOT requires FieldCriteiron");
				}
				return new NotCriterion(FieldCriterion.class.cast(c));
				
			default:
				assertTokenType(tree);
				return null;
		}
	}

	/**
	 * Creates a {@link Criterion} for the given function call.
	 * @param tree
	 * @param functionTable
	 * @return
	 */
	private Criterion readCriterionForFunctionCall(
		CommonTree tree, Map<String, MqlCriterionFunction> functionTable, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.FUNCTION_CALL);

		// get the function name
		String functionName = child(tree, 0).getText().trim().toLowerCase();
		Criterion ret = null;

		// function not found
		if (!functionTable.containsKey(functionName)) {
			throw new MqlException(
				"Unknown function: "+functionName);

		// no arguments
		} else if (tree.getChildCount()==1) {
			ret = functionTable.get(functionName).createForNoArguments();

		// criteria arguments
		} else if (child(tree, 1).getType()==MqlParser.CRITERIA) {
			Query query = new Query();
			readCriteria(child(tree, 1), query, ctx);
			ret = functionTable.get(functionName).createForQuery(query);

		// criteria arguments
		} else if (child(tree, 1).getType()==MqlParser.CRITERIA_GROUP_LIST) {
			QueryGroup queryGroup = new QueryGroup();
			readCriteriaGroupList(child(tree, 1), queryGroup, ctx);
			ret = functionTable.get(functionName).createForQueryGroup(queryGroup);

		// variable list arguments
		} else if (child(tree, 1).getType()==MqlParser.VARIABLE_LIST) {
			Object[] arguments = readVariableList(child(tree, 1), ctx);
			ret = functionTable.get(functionName).createForArguments(arguments);
		}

		// return it
		return ret;
	}

	private QueryGroup readCriteriaGroupList(CommonTree tree, QueryGroup queryGroup, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.CRITERIA_GROUP_LIST);
		if (queryGroup==null) {
			queryGroup = new QueryGroup();
		}
		for (int i=0; i<tree.getChildCount(); i++) {
			CommonTree groupCommonTree = child(tree, i);
			Query query = new Query();
			readCriteria(child(groupCommonTree, 0), query, ctx);
			queryGroup.add(query);
		}
		return queryGroup;
	}

	/**
	 * Reads a variable literal.
	 * @param tree
	 * @return
	 */
	private Object readVariableLiteral(CommonTree tree, ExecutionContext ctx) {
		String text = tree.getText();
		switch (tree.getType()){
			case MqlParser.FUNCTION_CALL:
				String functionName = child(tree, 0).getText().trim().toLowerCase();
				if (!variableFunctions.containsKey(functionName)) {
					throw new MqlException(
						"Unknown function: "+functionName);
				}
				Object[] args = tree.getChildCount()>1
					? readVariableList(child(tree, 1), ctx) : new Object[0];
				return variableFunctions.get(functionName).invoke(args);
			case MqlParser.PARAMETER:
				String name = child(tree, 0).getText();
				return ctx.getParameter(name, tree);
			case MqlParser.REGEX:
				return Pattern.compile(text.substring(1, text.length()-1));
			case MqlParser.INTEGER:
				return new Integer(text);
			case MqlParser.DECIMAL:
				return new Double(text);
			case MqlParser.DOUBLE_QUOTED_STRING:
				return evaluateString(text);
			case MqlParser.SINGLE_QUOTED_STRING:
				return evaluateString(text);
			case MqlParser.TRUE:
				return Boolean.TRUE;
			case MqlParser.FALSE:
				return Boolean.FALSE;
			case MqlParser.ARRAY:
				Object[] vars = new Object[child(tree, 0).getChildCount()];
				for (int i=0; i<vars.length; i++) {
					vars[i] = readVariableLiteral(child(child(tree, 0), i), ctx);
				}
				return vars;
			default:
				throw new MqlException(
					"Unknown variable literal type "+tree.getType()+" with value "+text);
		}
	}

	/**
	 * Evaluates a string.
	 * @param text
	 * @return
	 */
	private String evaluateString(String text) {
		text = text.substring(1, text.length()-1);
		int s=0;
		while (s<text.length()) {
			int idx = -1;
			
			idx = text.indexOf("\\n", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"\n"+text.substring(idx+2);
				s = idx+"\n".length();
				continue;
			}
			
			idx = text.indexOf("\\r", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"\r"+text.substring(idx+2);
				s = idx+"\r".length();
				continue;
			}
			
			idx = text.indexOf("\\t", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"\t"+text.substring(idx+2);
				s = idx+"\t".length();
				continue;
			}
			
			idx = text.indexOf("\\b", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"\b"+text.substring(idx+2);
				s = idx+"\b".length();
				continue;
			}
			
			idx = text.indexOf("\\f", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"\f"+text.substring(idx+2);
				s = idx+"\f".length();
				continue;
			}
			
			idx = text.indexOf("\\\"", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"\""+text.substring(idx+2);
				s = idx+"\"".length();
				continue;
			}
			
			idx = text.indexOf("\\'", s);
			if (idx!=-1) {
				text = text.substring(0, idx)+"'"+text.substring(idx+2);
				s = idx+"'".length();
				continue;
			}
			
			break;
		}
		return text;
	}

	/**
	 * Reads variable literals from a variable list.
	 * @param tree
	 * @return
	 */
	private Object[] readVariableList(CommonTree tree, ExecutionContext ctx) {
		assertTokenType(tree, MqlParser.VARIABLE_LIST);
		Object[] ret = new Object[tree.getChildCount()];
		for (int i=0; i<ret.length; i++) {
			ret[i] = readVariableLiteral(child(tree, i), ctx);
		}
		return ret;
	}

	/**
	 * Asserts a token is of an expected type.
	 * @param tree
	 * @param types
	 */
	private void assertTokenType(Tree tree, int... types) {
		if (tree==null) {
			throw new MqlException(
				"Got a null token when expecting a specific type");
		}
		int treeType = tree.getType();
		for (int type : types) {
			if (type==treeType) {
				return;
			}
		}
		throw new MqlException(
			"Unknown token: "+tree.toString());
	}

	/**
	 * Returns the child tree at the given index.
	 * @param tree
	 * @param idx
	 * @return
	 */
	private CommonTree child(Tree tree, int idx) {
		return CommonTree.class.cast(tree.getChild(idx));
	}

	/**
	 * Check to see if a tree is a string.
	 * @param tree
	 * @return
	 */
	private boolean isString(Tree tree) {
		return (tree!=null && (
			tree.getType()==MqlParser.DOUBLE_QUOTED_STRING
			|| tree.getType()==MqlParser.SINGLE_QUOTED_STRING));
	}

	/**
	 * Ensures that a variable was an expected type.
	 * @param value
	 * @param tree
	 * @param types
	 */
	private void assertType(Object value, CommonTree tree, Class<?>... types) {
		if (value==null) {
			return;
		}
		for (Class<?> type : types) {
			if (type.isInstance(value)) {
				return;
			}
		}
		throw new MqlException(tree, "Unexpected variable type");
	}

	/**
	 * A simple execution context.
	 */
	private class ExecutionContext {
		private Map<String, Object> params		= NO_PARAMS;
		private int currentParameterIndex 		= 0;
		private Object getParameter(String name, CommonTree tree) {
			if (name.equals("?")) {
				name = currentParameterIndex+"";
				currentParameterIndex++;
			}
			if (!params.containsKey(name)) {
				throw new MqlException(tree, "Parameter "+name+" was not found");
			}
			return params.get(name);
		}
	}

}
