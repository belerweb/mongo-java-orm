package com.googlecode.mjorm.mql;

import org.antlr.runtime.tree.CommonTree;

@SuppressWarnings("serial")
public class MqlException
	extends RuntimeException {

	public MqlException(Exception exception) {
		super(exception);
	}

	public MqlException(String message) {
		super(message);
	}

	public MqlException(String message, Exception exception) {
		super(message, exception);
	}

	public MqlException(CommonTree tree) {
		this(tree.getLine(), tree.getCharPositionInLine(), getNearText(tree));
	}

	public MqlException(CommonTree tree, String message) {
		this(tree.getLine(), tree.getCharPositionInLine(), getNearText(tree), message);
	}

	public MqlException(CommonTree tree, Exception exception) {
		this(tree.getLine(), tree.getCharPositionInLine(), getNearText(tree), exception);
	}

	public MqlException(int line, int col, String near) {
		super("Error on line: "+line+" column: "+col+" near: "+near);
	}

	public MqlException(int line, int col, String near, String message) {
		super(message+" on line: "+line+" column: "+col+" near: "+near);
	}

	public MqlException(int line, int col, String near, Exception exception) {
		super("Error on line: "+line+" column: "+col+" near: "+near, exception);
	}

	public static String getNearText(CommonTree tree) {
		return tree.getText();
	}
}
