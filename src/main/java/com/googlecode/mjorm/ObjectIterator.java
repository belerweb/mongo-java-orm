package com.googlecode.mjorm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * An ObjectIterator provides mapping features for
 * {@link DBCursors}s.  Basically it returns java
 * objects rather than {@link DBObject}s.
 * @param <E>
 */
public class ObjectIterator<E>
	implements Iterable<E>,
	Iterator<E> {

	private DBCursor cursor;
	private final ObjectMapper objectMapper;
	private final Class<E> clazz;

	/**
	 * Creates the {@link ObjectIterator}.
	 * @param cursor the cursor to wrap
	 * @param objectMapper the {@link ObjectMapper} to use
	 * @param clazz the class we're returning
	 */
	public ObjectIterator(
		DBCursor cursor, ObjectMapper objectMapper, Class<E> clazz) {
		this.cursor 		= cursor;
		this.objectMapper	= objectMapper;
		this.clazz			= clazz;
	}

	/**
	 * For for loops.
	 */
	public Iterator<E> iterator() {
		return this;
	}

	/**
	 * Reads all of the objects behind this cursor
	 * and returns them in a {@link List}.
	 * @param ret the list to read the objects into
	 * @return the {@link List} of objects.
	 */
	public List<E> readAll(List<E> ret) {
		while (hasNext()) {
			ret.add(next());
		}
		return ret;
	}

	/**
	 * Reads all of the objects behind this cursor
	 * and returns them in a {@link List}.
	 * @return the {@link List} of objects.
	 */
	public List<E> readAll() {
		return readAll(new ArrayList<E>());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return cursor.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public E next() {
		try {
			return objectMapper.map(cursor.next(), clazz);
		} catch (Exception e) {
			throw new MjormException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public E current() {
		try {
			return objectMapper.map(cursor.curr(), clazz);
		} catch (Exception e) {
			throw new MjormException(e);
		}
	}

	/**
	 * {@see DBCursor#remove()}.
	 */
	public void remove() {
		cursor.remove();
	}

	/**
	 * Returns the underlying {@link DBCursor}.
	 * @return the {@link DBCursor}
	 */
	public DBCursor getDBCursor() {
		return cursor;
	}

}
