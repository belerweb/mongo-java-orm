package com.googlecode.mjorm;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

/**
 * Utilities for working with {@link DBObject}s.
 */
public class DBObjectUtil {

	/**
	 * Indicates whether or not the given {@link Object}
	 * is a {@link BasicDBList}.
	 * @param dbObject
	 * @return
	 */
	public static boolean isList(Object dbObject) {
		return BasicDBList.class.isInstance(dbObject);
	}

	/**
	 * Indicates whether or not the given {@link Object}
	 * is a {@link DBObject}.
	 * @param dbObject
	 * @return
	 */
	public static boolean isDBObject(Object dbObject) {
		return DBObject.class.isInstance(dbObject);
	}

	/**
	 * Merges the objects using a a {@link MergeConflictResolver}
	 * that throws an {@link IllegalStateException}.
	 * @see DBObjectUtil#THROW_EXCEPTION
	 * @param from
	 * @param into
	 */
	public static void merge(DBObject from, DBObject into) {
		merge(from, into, THROW_EXCEPTION);
	}

	/**
	 * Merges the objects using the given {@link MergeConflictResolver}
	 * to resolve merge conflicts.
	 * @see DBObjectUtil#THROW_EXCEPTION
	 * @param from
	 * @param into
	 */
	public static void merge(DBObject from, DBObject into, MergeConflictResolver resolver) {
		for (String field : from.keySet()) {

			// new
			if (!into.containsField(field)) {
				into.put(field, from.get(field));
				continue;
			}

			// existing
			Object fromVal = from.get(field);
			Object intoVal = into.get(field);

			// they're the same
			if (fromVal.equals(intoVal)) {
				continue;
			}
			
			// they're not DB objects, resolve them
			if (!isDBObject(intoVal)
				|| !isDBObject(fromVal)) {
				resolver.resolve(from, into, field);
				continue;
			}

			// we don't merge lists
			if (isList(intoVal) || isList(fromVal)) {
				resolver.resolve(from, into, field);
				continue;
			}
	
			// merge the sub objects
			merge(
				DBObject.class.cast(fromVal),
				DBObject.class.cast(intoVal),
				resolver);
		}
	}

	/**
	 * Gets a nested property of an obect.
	 * @param dbObject the object
	 * @param name the property
	 * @return the object
	 */
	public static Object getNestedProperty(DBObject dbObject, String name) {
		String[] names = name.split("\\.");
		Object ret = null;
		boolean hasMore = false;
		for (int i=0; i<names.length; i++) {
			hasMore = i < names.length-1;
			ret = dbObject.get(names[i]);
			if (hasMore && DBObject.class.isInstance(ret)) {
				dbObject = DBObject.class.cast(ret);
			} else if (hasMore) {
				throw new IllegalArgumentException(
					"Property "+name+" could not be followed");
			}
		}
		return ret;
	}

	/**
	 * Resolves merge conflicts during
	 * {@link DBObjectUtil#merge(DBObject, DBObject, MergeConflictResolver).
	 */
	public interface MergeConflictResolver {
		void resolve(DBObject from, DBObject into, String field);
	}

	/**
	 * A {@link MergeConflictResolver} that throws an
	 * {@link IllegalStateException} on a merge conflict.
	 */
	public static final MergeConflictResolver THROW_EXCEPTION = new MergeConflictResolver() {
		public void resolve(DBObject from, DBObject into, String field) {
			Object fromVal 	= from.get(field);
			Object toVal	= into.get(field);
			if (!fromVal.equals(toVal)) {
				throw new IllegalStateException("Unable to merge "+field);
			}
		}
	};

	
}
