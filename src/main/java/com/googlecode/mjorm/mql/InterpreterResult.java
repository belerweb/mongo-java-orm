package com.googlecode.mjorm.mql;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class InterpreterResult {

	public static final InterpreterResult NO_RESULT
		= new InterpreterResult(null, null, null);

	private DBCursor cursor;
	private DBObject object;
	private WriteResult result;

	public InterpreterResult(DBCursor cursor, DBObject object, WriteResult result) {
		this.cursor		= cursor;
		this.object		= object;
		this.result		= result;
	}

	/**
	 * @return the cursor
	 */
	protected DBCursor getCursor() {
		return cursor;
	}

	/**
	 * @return the object
	 */
	protected DBObject getObject() {
		return object;
	}

	/**
	 * @return the result
	 */
	protected WriteResult getResult() {
		return result;
	}

}
