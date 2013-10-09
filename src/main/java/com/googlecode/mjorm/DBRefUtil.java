package com.googlecode.mjorm;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Utilities for working with {@link DBRef} objects.
 */
public final class DBRefUtil {

	/**
	 * Does nothing.
	 */
	private DBRefUtil() { }
	static { new DBRefUtil(); }

	/**
	 * Inspects the given {@link DBObject} recursively and fetches it's
	 * DBRefs recursively for the given {@link DB}.
	 * @param db the {@link DB}
	 * @param obj the {@link DBObject}
	 * @return the {@link DBObject} that was passed in
	 */
	public static DBObject fetchDBRefs(DB db, DBObject obj) {

		// BasicDBList
		if (BasicDBList.class.isInstance(obj)) {
			BasicDBList list = BasicDBList.class.cast(obj);
			for (int i=0; i<list.size(); i++) {
				Object value = fetchDBRef(db, list.get(i));
				if (DBObject.class.isInstance(value)) {
					fetchDBRefs(db, DBObject.class.cast(value));
				}
				list.set(i, value);
			}

		// BasicDBObject
		} else if (BasicDBObject.class.isInstance(obj)) {
			BasicDBObject dbObject = BasicDBObject.class.cast(obj);

			// loop through each key
			for (String key : obj.keySet()) {
				Object value = fetchDBRef(db, dbObject.get(key));
				if (DBObject.class.isInstance(value)) {
					fetchDBRefs(db, DBObject.class.cast(value));
				}
				dbObject.put(key, value);
			}
		}

		// return it
		return obj;
	}

	/**
	 * Fetches the {@link DBRef} if the given object is one.
	 * @param db the {@link DB}
	 * @param obj the object
	 * @return the value
	 */
	public static Object fetchDBRef(DB db, Object obj) {
		if (BasicDBObject.class.isInstance(obj)) {
			BasicDBObject dbObject = BasicDBObject.class.cast(obj);
			if (isRef(dbObject)
				&& (!dbObject.containsField("$db")
				|| dbObject.get("$db").equals(db.getName()))) {
				return new DBRef(db, dbObject).fetch();
			}
		}
		return obj;
	}

	/**
	 * Checks whether or not the given {@link DBObject} is a
	 * {@link DBRef}.
	 * @param obj the {@link DBObject}
	 * @return true if it is
	 */
	public static boolean isRef(DBObject obj) {
		return (obj.containsField("$ref") && obj.containsField("$id"));
	}
}
