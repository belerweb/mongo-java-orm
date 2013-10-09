package com.googlecode.mjorm.mql;

import java.util.Map;

import com.googlecode.mjorm.ObjectIterator;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public interface Statement {

	/**
	 * Sets the named parameters uses by this statement.
	 * @param params
	 */
	Statement setParameters(Map<String, Object> params);

	/**
	 * Sets the indexed parameters used by this statement.
	 * @param params
	 */
	Statement setParameters(Object... params);

	/**
	 * Sets a parameter by it's name.
	 * @param name
	 * @param param
	 */
	Statement setParameter(String name, Object param);

	/**
	 * Sets a parameter by it's index
	 * @param index t
	 * @param param
	 */
	Statement setParameter(int index, Object param);

	/**
	 * Clears all of the set parameters.
	 */
	Statement clearParameters();

	/**
	 * Executes the statement expecting results.
	 * @return
	 */
	DBCursor execute();

	/**
	 * Executes the statement expecting a single result.
	 * @return
	 */
	DBObject executeSingle();
	
	/**
	 * Executes the statement expecting results and transforming
	 * them into the given class.
	 * @param clazz
	 * @return
	 */
	<T> ObjectIterator<T> execute(Class<T> clazz);

	/**
	 * Executes the statement expecting a single result and
	 * transforms it into the given class.
	 * @param clazz
	 * @return
	 */
	<T> T executeSingle(Class<T> clazz);

	/**
	 * Executes the statement expecting no results.
	 */
	void executeUpdate();
}
