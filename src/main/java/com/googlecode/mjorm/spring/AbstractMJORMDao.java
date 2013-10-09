package com.googlecode.mjorm.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.googlecode.mjorm.mql.Statement;
import com.googlecode.mjorm.query.DaoQuery;
import com.googlecode.mjorm.spring.MongoDBDaoSupport;
import com.mongodb.WriteConcern;

/**
 * An abstract MJORM dao.
 *
 */
public class AbstractMJORMDao
	extends MongoDBDaoSupport {

	private boolean cacheStatements = true;
	private Map<String, Statement> statementCache = new HashMap<String, Statement>();

	/**
	 * Converts the given object id string into
	 * a mongo {@link ObjectId}.
	 * @param ids the ids
	 * @return the ObjectId List
	 */
	protected ObjectId[] oids(String... ids) {
		ObjectId[] ret = new ObjectId[ids.length];
		for (int i=0; i<ids.length; i++) {
			ret[i] = ids[i]!=null ? new ObjectId(ids[i]) : null;
		}
		return ret;
	}

	/**
	 * Converts the given object id string into
	 * a mongo {@link ObjectId}.
	 * @param ids the ids
	 * @return the ObjectId List
	 */
	protected List<ObjectId> oids(Collection<String> ids) {
		List<ObjectId> ret = new ArrayList<ObjectId>(ids.size());
		for (String id : ids) {
			ret.add(id!=null ? new ObjectId(id) : null);
		}
		return ret;
	}

	/**
	 * Converts the given object id string into
	 * a mongo {@link ObjectId}.
	 * @param objectId the id
	 * @return the ObjectId string
	 */
	protected ObjectId oid(String objectId) {
		return objectId!=null ? new ObjectId(objectId) : null;
	}

	/**
	 * Creates an object.
	 * @param collection the collection
	 * @param object the object
	 * @param <T> the type
	 * @return the created object
	 */
	protected <T> T createObject(String collection, T object) {
		return getMongoDao().createObject(collection, object);
	}

	/**
	 * Creates an object.
	 * @param collection the collection
	 * @param object the object
	 * @param concern the WriteConcern
	 * @param <T> the type
	 * @return the created object
	 */
	protected <T> T createObject(String collection, T object, WriteConcern concern) {
		return getMongoDao().createObject(collection, object, concern);
	}

	/**
	 * Reads an object from the given collection.
	 * @param collection the collection
	 * @param id the id
	 * @param clazz the type
	 * @param <T> the type
	 * @return the object
	 */
	protected <T> T readObject(String collection, Object id, Class<T> clazz) {
		return getMongoDao().readObject(collection, id, clazz);
	}

	/**
	 * Updates an existing object.
	 * @param collection the collection
	 * @param id the id
	 * @param o the object
	 */
	protected void updateObject(String collection, Object id, Object o) {
		getMongoDao().updateObject(collection, id, o);
	}

	/**
	 * Updates an existing object.
	 * @param collection the collection
	 * @param id the id
	 * @param concern the WriteConcern
	 * @param o the object
	 */
	protected void updateObject(String collection, Object id, Object o, WriteConcern concern) {
		getMongoDao().updateObject(collection, id, o, concern);
	}

	/**
	 * Deletes an existing object.
	 * @param collection the collection
	 * @param id the id
	 */
	protected void deleteObject(String collection, Object id) {
		getMongoDao().deleteObject(collection, id);
	}

	/**
	 * Deletes an existing object.
	 * @param collection the collection
	 * @param id the id
	 * @param concern the WriteConcern
	 */
	protected void deleteObject(String collection, Object id, WriteConcern concern) {
		getMongoDao().deleteObject(collection, id, concern);
	}

	/**
	 * Creates a {@link DaoQuery}.
	 * @return the query
	 */
	protected DaoQuery createQuery() {
		return getMongoDao().createQuery();
	}

	/**
	 * Creates a {@link DaoQuery}.
	 * @param collectionName the collection name
	 * @return the query
	 */
	protected DaoQuery createQuery(String collectionName) {
		return createQuery().setCollection(collectionName);
	}

	/**
	 * Creates a {@link Statement} from the given mql
	 * optionally caching it for later use.
	 * @param mql the MQL
	 * @return the {@link Statement}
	 */
	protected Statement createStatement(String mql) {
		if (cacheStatements && statementCache.containsKey(mql)) {
			return statementCache.get(mql);
		}
		Statement ret = getMongoDao().createStatement(mql);
		if (cacheStatements) {
			statementCache.put(mql, ret);
		}
		return ret;
	}
}
