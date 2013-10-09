package com.googlecode.mjorm;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.mjorm.mql.MqlException;
import com.googlecode.mjorm.mql.Statement;
import com.googlecode.mjorm.mql.StatementImpl;
import com.googlecode.mjorm.query.DaoQuery;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBEncoder;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.WriteConcern;

/**
 * Basic implementation of the {@link MongoDao} interface.
 */
public class MongoDaoImpl
	implements MongoDao {

	private DB db;
	private ObjectMapper objectMapper;

	/**
	 * Creates the {@link MongoDaoImpl}.
	 * @param db the {@link DB}
	 * @param objectMapper the {@link ObjectMapper}
	 */
	public MongoDaoImpl(DB db, ObjectMapper objectMapper) {
		this.db 			= db;
		this.objectMapper	= objectMapper;
	}

	/**
	 * Creates the {@link MongoDaoImpl}.
	 */
	public MongoDaoImpl() {
		this(null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(String mql) {
		try {
			return new StatementImpl(
				new ByteArrayInputStream(mql.getBytes()), db, objectMapper);
		} catch(Exception e) {
			throw new MqlException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DaoQuery createQuery() {
		return new DaoQuery()
			.setDB(this.db)
			.setObjectMapper(this.objectMapper);
	}

	/**
	 * {@inheritDoc}
	 */
	public long countObjects(String collection, DBObject query) {
		return getCollection(collection).count(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T createObject(String collection, T object, WriteConcern concern) {
		DBObject dbObject;
		try {
			dbObject = objectMapper.unmap(object);
			getCollection(collection).insert(dbObject, concern);
			return (T)objectMapper.map(dbObject, object.getClass());
		} catch (Exception e) {
			throw new MjormException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T createObject(String collection, T object) {
		return createObject(collection, object, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] createObjects(String collection, T[] objects) {
		return createObjects(collection, objects, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] createObjects(String collection, T[] objects, WriteConcern concern) {
		DBObject[] dbObjects = new DBObject[objects.length];
		try {
			for (int i=0; i<objects.length; i++) {
				dbObjects[i] = objectMapper.unmap(objects[i]);
			}
			getCollection(collection).insert(dbObjects, concern);
			T[] ret = (T[])Array.newInstance(objects[0].getClass(), objects.length);
			for (int i=0; i<objects.length; i++) {
				ret[i] = (T)objectMapper.map(dbObjects[i], objects[i].getClass());
			}
		} catch (Exception e) {
			throw new MjormException(e);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObject(String collection, Object id, WriteConcern concern) {
		deleteObjects(collection, new BasicDBObject("_id", objectMapper.unmapValue(id)), concern);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObject(String collection, Object id) {
		deleteObject(collection, id, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObjects(String collection, Object[] ids, WriteConcern concern) {
		deleteObjects(collection, 
			new BasicDBObject("_id", new BasicDBObject("$in", objectMapper.unmapValue(ids))), concern);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObjects(String collection, Object[] ids) {
		deleteObjects(collection, ids, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObjects(String collection, DBObject query, WriteConcern concern) {
		getCollection(collection).remove(query, concern);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObjects(String collection, DBObject query) {
		deleteObjects(collection, query, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getPartialObject(String collection, Object id, String name, Class<T> clazz) {
		return getPartialObject(collection, new BasicDBObject("_id", objectMapper.unmapValue(id)), name, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPartialObject(String collection, DBObject query, String name, Class<T> clazz) {

		// query for the object
		DBObject dbObject = getCollection(collection).findOne(
			query, new BasicDBObject(name, 1));
		if (dbObject==null) {
			return null;
		}

		// now recurse down the object
		Object value = null;
		for (String part : name.split("\\.")) {
			if (!dbObject.containsField(part)) {
				return null;
			}
			value = dbObject.get(part);
			if (DBObject.class.isInstance(value)) {
				dbObject = DBObject.class.cast(value);
			} else {
				break;
			}
		}

		// now convert
		return !isPrimitive(clazz)
			? objectMapper.map(dbObject, clazz)
			: (T)value;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void savePartialObject(
		String collection, Object id, String name, T data, boolean upsert, WriteConcern concern) {
		savePartialObject(collection, new BasicDBObject("_id", objectMapper.unmapValue(id)), name, data, upsert);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void savePartialObject(
		String collection, Object id, String name, T data, boolean upsert) {
		savePartialObject(collection, new BasicDBObject("_id", objectMapper.unmapValue(id)),
			name, data, upsert, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void savePartialObject(
		String collection, DBObject query, String name, T data, boolean upsert) {
		savePartialObject(collection, query, name, data, upsert, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void savePartialObject(
		String collection, DBObject query, String name, T data, boolean upsert, WriteConcern concern) {

		// the value we're storing
		Object value = null;

		// if it's not null, determine the
		// type and store accordingly
		if (data!=null) {
			Class<?> clazz = data.getClass();
			value = !isPrimitive(clazz)
				? objectMapper.unmap(data)
				: data;
		}

		// save it
		getCollection(collection).update(
			query, new BasicDBObject("$set", new BasicDBObject(name, value)),
			upsert, false, concern);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePartialObject(String collection, DBObject query, String name, WriteConcern concern) {
		getCollection(collection).update(
			query, new BasicDBObject("$unset", new BasicDBObject(name, 1)),
			false, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePartialObject(String collection, DBObject query, String name) {
		deletePartialObject(collection, query, name, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePartialObject(String collection, Object id, String name, WriteConcern concern) {
		deletePartialObject(collection, new BasicDBObject("_id", objectMapper.unmapValue(id)), name, concern);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePartialObject(String collection, Object id, String name) {
		deletePartialObject(collection, id, name, getCollection(collection).getWriteConcern());
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findByExample(String collection, T example, Class<T> clazz) {
		DBObject query;
		try {
			query = objectMapper.unmap(example);
		} catch (Exception e) {
			throw new MjormException(e);
		}
		return findObjects(collection, query, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T findObject(String collection, DBObject query, Class<T> clazz) {
		DBObject dbObject = getCollection(collection).findOne(query);
		try {
			return objectMapper.map(dbObject, clazz);
		} catch (Exception e) {
			throw new MjormException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findObjects(
		String collection, DBObject query, Class<T> clazz) {
		DBCursor cursor = getCollection(collection).find(query);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public DBCollection getCollection(String name) {
		return db.getCollection(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public DB getDB() {
		return db;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T readObject(String collection, Object id, Class<T> clazz) {
		DBObject dbObject = getCollection(collection)
			.findOne(new BasicDBObject("_id", objectMapper.unmapValue(id)));
		try {
			return objectMapper.map(dbObject, clazz);
		} catch (Exception e) {
			throw new MjormException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] readObjects(String collection, Object[] ids, Class<T> clazz) {
		DBCursor cursor = getCollection(collection).find(
			new BasicDBObject("_id", new BasicDBObject("$in", objectMapper.unmapValue(ids))));
		try {
			List<T> ret = new ArrayList<T>();
			while (cursor.hasNext()) {
				ret.add((T)objectMapper.map(cursor.next(), clazz));
			}
			return ret.toArray((T[])Array.newInstance(clazz, 0));
		} catch (Exception e) {
			throw new MjormException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateObject(String collection, Object id, Object o, WriteConcern concern) {
		DBObject dbObject;
		try {
			dbObject = objectMapper.unmap(o);
		} catch (Exception e) {
			throw new MjormException(e);
		}
		 getCollection(collection).update(
				new BasicDBObject("_id", objectMapper.unmapValue(id)), dbObject, false, false, concern);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateObject(String collection, Object id, Object o) {
		DBObject dbObject;
		try {
			dbObject = objectMapper.unmap(o);
		} catch (Exception e) {
			throw new MjormException(e);
		}
		 getCollection(collection).update(new BasicDBObject("_id", objectMapper.unmapValue(id)), dbObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult executeCommand(DBObject cmd) {
		CommandResult result = getDB().command(cmd);
		result.throwOnError();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult executeCommand(String cmd) {
		CommandResult result = getDB().command(cmd);
		result.throwOnError();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void ensureIndex(
		String collection, String key,
		boolean background, boolean unique, boolean dropDupes) {
		ensureIndex(collection, new BasicDBObject(key, 1), background, unique, dropDupes);
	}

	/**
	 * {@inheritDoc}
	 */
	public void ensureIndex(
		String collection, DBObject keys,
		boolean background, boolean unique, boolean dropDupes) {
		getCollection(collection).ensureIndex(keys,
			BasicDBObjectBuilder.start()
				.add("unique", unique)
				.add("dropDups", dropDupes)
				.add("background", background)
				.get());
	}

	/**
	 * {@inheritDoc}
	 */
	public MapReduceResult mapReduce(String collection, MapReduce mapReduce) {

		if (mapReduce.getOutputCollectionName()==null
			|| mapReduce.getOutputCollectionName().trim().length()==0) {
			throw new IllegalArgumentException("Invalid output collection name");
		}

		// create command
		MapReduceCommand cmd = new MapReduceCommand(
			getCollection(collection),
			mapReduce.getMapFunction(),
			mapReduce.getReduceFunction(),
			mapReduce.getOutputCollectionName(),
			mapReduce.getOutputType(),
			mapReduce.getQuery());
		
		if (mapReduce.getSort()!=null) {
			cmd.setSort(mapReduce.getSort());
		}
		if (mapReduce.getLimit()!=null) {
			cmd.setLimit(mapReduce.getLimit().intValue());
		}
		if (mapReduce.getFinalizeFunction()!=null) {
			cmd.setFinalize(mapReduce.getFinalizeFunction());
		}
		if (mapReduce.getScope()!=null) {
			cmd.setScope(mapReduce.getScope());
		}
		if (mapReduce.getVerbose()!=null) {
			cmd.setVerbose(mapReduce.getVerbose());
		}
		if (mapReduce.getOutputDBName()!=null) {
			cmd.setOutputDB(mapReduce.getOutputDBName());
		}

		// execute and return
		return new MapReduceResult(
			getCollection(collection).mapReduce(cmd));
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T findAndDelete(
		String collection, DBObject query,
		DBObject sort, Class<T> clazz, String[] fields) {
		DBObject fieldsObject = null;
		if (fields!=null && fields.length>0) {
			fieldsObject = new BasicDBObject();
			for (String field : fields) {
				fieldsObject.put(field, 1);
			}
		}
		return objectMapper.map(
			getCollection(collection).findAndModify(
				query, fieldsObject, sort, true, null, false, false), clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T findAndDelete(
		String collection, DBObject query, DBObject sort, Class<T> clazz) {
		return findAndDelete(collection, query, sort, clazz, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T findAndModify(
		String collection, DBObject query, DBObject sort, DBObject update,
		boolean returnNew, boolean upsert, Class<T> clazz, String[] fields) {
		DBObject fieldsObject = null;
		if (fields!=null && fields.length>0) {
			fieldsObject = new BasicDBObject();
			for (String field : fields) {
				fieldsObject.put(field, 1);
			}
		}
		return objectMapper.map(
			getCollection(collection).findAndModify(
				query, fieldsObject, sort, false, update, returnNew, upsert), clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T findAndModify(
		String collection, DBObject query, DBObject sort, DBObject update,
		boolean returnNew, boolean upsert, Class<T> clazz) {
		return findAndModify(collection, query, sort, update, returnNew, upsert, clazz, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(
		String collection, DBObject query, DBObject update,
		boolean upsert, boolean multi, WriteConcern concern, DBEncoder encoder) {
		getCollection(collection).update(query, update, upsert, multi, concern, encoder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(String collection, DBObject query, DBObject update,
		boolean upsert, boolean multi, WriteConcern concern) {
		update(collection, query, update, upsert, multi, concern, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(
		String collection, DBObject query, DBObject update,
		boolean upsert, boolean multi) {
		update(collection, query, update, upsert, multi, null, null);
	}

	/**
	 * Quick and easy check for primitives.
	 * @param clazz the class
	 * @return true if primitive
	 */
	private boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive()
			|| clazz.equals(Byte.class)
			|| clazz.equals(Short.class)
			|| clazz.equals(Integer.class)
			|| clazz.equals(Long.class)
			|| clazz.equals(Float.class)
			|| clazz.equals(Double.class)
			|| clazz.equals(Boolean.class)
			|| clazz.equals(Character.class)
			|| clazz.equals(String.class)
			|| clazz.equals(Byte.class);
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(DB db) {
		this.db = db;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
