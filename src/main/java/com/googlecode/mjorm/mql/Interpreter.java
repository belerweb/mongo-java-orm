package com.googlecode.mjorm.mql;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

public interface Interpreter {

	/**
	 * Compiles the given code and returns the AST.
	 * @param ips
	 * @return
	 * @throws IOException
	 * @throws RecognitionException
	 */
	Tree compile(InputStream ips)
		throws IOException, RecognitionException;

	/**
	 * Interprets the given AST and returns an {@link InterpreterResult}
	 * for each command that was executed.
	 * @param tree
	 * @param parameters
	 * @return
	 */
	List<InterpreterResult> interpret(Tree tree, Map<String, Object> parameters);

	/**
	 * Interprets the given AST and returns an {@link InterpreterResult}
	 * for each command that was executed.
	 * @param tree
	 * @return
	 */
	List<InterpreterResult> interpret(Tree tree, Object... parameters);

	/**
	 * Interprets the given AST and returns an {@link InterpreterResult}
	 * for each command that was executed.
	 * @param tree
	 * @return
	 */
	List<InterpreterResult> interpret(Tree tree);

}
