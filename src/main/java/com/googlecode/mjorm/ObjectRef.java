package com.googlecode.mjorm;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.DBRef;

/**
 * A reference to another object.
 */
@SuppressWarnings("serial")
public class ObjectRef
	implements Serializable {

	private String id;
	private String collection;
	private String database;

	/**
	 * The ref.
	 */
	public ObjectRef() {
		// no-op
	}

	/**
	 * Creates the object ref.
	 * @param id the id
	 * @param collection the collection
	 * @param database the database name
	 */
	public ObjectRef(String id, String collection, String database) {
		this.id			= id;
		this.collection	= collection;
		this.database	= database;
	}

	/**
	 * Creates the object ref.
	 * @param id the id
	 * @param collection the collection
	 */
	public ObjectRef(String id, String collection) {
		this(id, collection, null);
	}

	/**
	 * Returns the {@code ObjectRef} as a {@link DBRef}.
	 * @param db the {@link DB}
	 * @return the {@link DBRef}
	 */
	public DBRef asDBRef(DB db) {
		return new DBRef(
			db, this.collection, new ObjectId(this.database));
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

}
