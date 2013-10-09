package com.googlecode.mjorm;

import com.mongodb.DBObject;

/**
 * The {@code ObjectMapper} is responsible for converting
 * objects to and from mongo's {@link DBObject}s.
 */
public interface ObjectMapper {

	/**
	 * Converts the given {@link DBObject} into a java object.
	 * @param <T> the type
	 * @param dbObject the {@link DBObject}
	 * @param objectClass the {@link Class} of the object to convert to
	 * @throws MjormException on error
	 * @return the java object
	 */
	<T> T map(DBObject dbObject, Class<T> objectClass)
		throws MjormException;

	/**
	 * Converts the given java object into a {@link DBObject}.
	 * @param <T> the type
	 * @param object the java object
	 * @throws MjormException on error
	 * @return the {@link DBObject}
	 */
	<T> DBObject unmap(T object)
		throws MjormException;

	/**
	 * Converts the given java object into an object suitable for
	 * storage in the database.
	 * @param <T> the type
	 * @param object the java object
	 * @throws MjormException on error
	 * @return the {@link Object}
	 */
	<T> Object unmapValue(T object)
		throws MjormException;

}
