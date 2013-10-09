package com.googlecode.mjorm.mql;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import com.googlecode.mjorm.ObjectIterator;
import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class StatementImpl
	implements Statement {

	private static final Logger LOGGER = Logger.getLogger(StatementImpl.class.getName());

	private Tree tree;
	private Map<String, Object> parameters;
	private Interpreter interpreter;
	private ObjectMapper objectMapper;

	/**
	 * Creates the {@link StatementImpl}
	 * @param mql {@link InputStream} that contains MQL code
	 * @param db the mongo {@link DB}
	 * @param objectMapper the {@link ObjectMapper}
	 * @throws IOException on error
	 * @throws RecognitionException on error
	 */
	public StatementImpl(InputStream mql, DB db, ObjectMapper objectMapper)
		throws IOException, RecognitionException {
		try {
			this.interpreter = InterpreterFactory.getDefaultInstance().create(db, objectMapper);
			this.tree = interpreter.compile(mql);
		} catch(Exception e) {
			throw new MqlException(e);
		}
		this.parameters = new HashMap<String, Object>();
		this.objectMapper = objectMapper;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement setParameters(Map<String, Object> params) {
		parameters.clear();
		parameters.putAll(params);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement setParameters(Object... params) {
		parameters.clear();
		for (int i=0; i<params.length; i++) {
			parameters.put(i+"", params[i]);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement setParameter(String name, Object param) {
		parameters.put(name, param);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement setParameter(int index, Object param) {
		parameters.put(index+"", param);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement clearParameters() {
		parameters.clear();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public DBCursor execute() {
		InterpreterResult res = executeInternal();
		if (res.getCursor()==null) {
			throw new MqlException("Expected a DBCursor and din't get one");
		}
		return res.getCursor();
	}

	/**
	 * {@inheritDoc}
	 */
	public DBObject executeSingle() {
		InterpreterResult res = executeInternal();
		if (res.getObject()!=null) {
			return res.getObject();
		} else if (res.getCursor()!=null) {
			DBCursor cursor = res.getCursor();
			DBObject ret = (cursor.hasNext()) ? cursor.next() : null;
			cursor.close();
			return ret;
		}
		throw new MqlException("Expected a DBCursor or DBObject and din't get one");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> execute(Class<T> clazz) {
		return new ObjectIterator<T>(execute(), objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T executeSingle(Class<T> clazz) {
		return objectMapper.map(executeSingle(), clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public void executeUpdate() {
		executeInternal();
	}


	/**
	 * Executes the statement and returns the very
	 * last {@link InterpreterResult}.
	 * @return
	 */
	private InterpreterResult executeInternal() {
		List<InterpreterResult> res = interpreter.interpret(tree, parameters);
		if (res.isEmpty()) {
			throw new MqlException("No InterpreterResult was returned");
		} else if (res.size()>1) {
			LOGGER.warning(
				"interpretation returned more than one "
				+"InterpreterResult, using the last");
		}
		return res.get(res.size()-1);
	}

}
