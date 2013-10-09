package com.googlecode.mjorm.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.ObjectIterator;
import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.ReadPreference;

public class DaoQuery
	extends AbstractQueryCriterion<DaoQuery> {

	private DB db;
	private ObjectMapper objectMapper;
	private Map<String, Integer> sort;
	private Map<String, Object> specials;
	private Integer firstDocument;
	private Integer maxDocuments;
	private Integer batchSize;
	private DBObject hint;
	private Boolean snapShot;
	private String comment;
	private String collection;
	private ReadPreference readPreference;
	private CursorVisitor cursorVisitor;
	private DaoModifier modifier;

	/**
	 * Allows for the visiting of the {@link DBCursor}
	 * before it is returned as an {@link ObjectIterator}
	 * form the various query methods of this class.
	 */
	public static interface CursorVisitor {
		void visit(DBCursor cursor);
	}

	/**
	 * Creates the {@link DaoQuery}.
	 */
	public DaoQuery() {
		this.clear();
	}

	/**
	 * Clears the query.
	 */
	public void clear() {
		super.clear();
		if (modifier!=null) {
			modifier.clear();
		}
		sort 			= new HashMap<String, Integer>();
		specials 		= new HashMap<String, Object>();
		firstDocument	= null;
		maxDocuments	= null;
		batchSize		= null;
		hint			= null;
		snapShot		= null;
		comment			= null;
		collection		= null;
		cursorVisitor	= null;
		modifier		= null;
	}

	/**
	 * Asserts that the {@link DaoQuery} is valid.
	 * Throws an exception if not.
	 */
	public void assertValid() {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		} else if (db==null) {
			throw new IllegalStateException("DB must be specified");
		}
	}

	/**
	 * Creates a {@link DaoModifier} for the
	 * current query.
	 * @return the {@link DaoModifier}
	 */
	public DaoModifier modify() {
		if (modifier==null) {
			modifier = new DaoModifier(this);
		}
		return modifier;
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param clazz the type of objects to return
	 * @return the iterator.
	 */
	public <T> ObjectIterator<T> findObjects(Class<T> clazz) {
		assertValid();
		DBCursor cursor = db.getCollection(collection).find(toQueryObject(objectMapper));
		setupCursor(cursor);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param fields the fields to return
	 * @return the iterator.
	 */
	public DBCursor findObjects(DBObject fields) {
		assertValid();
		DBCursor cursor = db.getCollection(collection).find(toQueryObject(objectMapper), fields);
		setupCursor(cursor);
		return cursor;
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param clazz the type of objects to return
	 * @return the iterator.
	 */
	public DBCursor findObjects() {
		assertValid();
		DBCursor cursor = db.getCollection(collection).find(toQueryObject(objectMapper));
		setupCursor(cursor);
		return cursor;
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param fields the type of objects to return
	 * @return the iterator.
	 */
	public DBCursor findObjects(String... fields) {
		DBObject dbObject = new BasicDBObject();
		for (String field : fields) {
			dbObject.put(field, 1);
		}
		return findObjects(dbObject);
	}

	/**
	 * Executes the query and returns an object of the given type.
	 * @param clazz the type of object to return
	 * @return the object.
	 */
	public <T> T findObject(Class<T> clazz) {
		assertValid();
		ObjectIterator<T> itr = findObjects(clazz);
		return itr.hasNext() ? itr.next() : null;
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param clazz the type of objects to return
	 * @return the iterator.
	 */
	public DBObject findObject(DBObject fields) {
		assertValid();
		DBCursor itr = findObjects(fields);
		return itr.hasNext() ? itr.next() : null;
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param clazz the type of objects to return
	 * @return the iterator.
	 */
	public DBObject findObject() {
		assertValid();
		DBCursor itr = findObjects();
		return itr.hasNext() ? itr.next() : null;
	}

	/**
	 * Executes the query and returns the number of objects
	 * that it would return.
	 * @return the count
	 */
	public long countObjects() {
		assertValid();
		return (readPreference!=null)
			? db.getCollection(collection).count(toQueryObject(objectMapper), readPreference)
			: db.getCollection(collection).count(toQueryObject(objectMapper));
	}


	/**
	 * Returns distinct values for the given field.  This field
	 * passed must be the name of a field on a MongoDB document.
	 * @param field the field
	 * @return the distinct objects
	 */
	@SuppressWarnings("unchecked")
	public List<Object> distinct(String field) {
		assertValid();
		DBCollection col = db.getCollection(collection);
		return (readPreference!=null)
			? col.distinct(field, toQueryObject(objectMapper), readPreference)
			: col.distinct(field, toQueryObject(objectMapper));
	}

	/**
	 * Returns distinct values for the given field.  This field
	 * passed must be the name of a field on a MongoDB document.
	 * @param field the field
	 * @param expected the expected type
	 * @return the distinct objects
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> distinct(String field, Class<T> expected) {
		assertValid();
		return (List<T>)distinct(field);
	}

	/**
	 * Explains the current query.
	 * @return the explained query
	 */
	public DBObject explain() {
		assertValid();
		DBCursor cursor = db.getCollection(collection).find(toQueryObject(objectMapper));
		setupCursor(cursor);
		return cursor.explain();
	}

	/**
	 * Sets up a {@link DBCursor} for this query.
	 * @param cursor the curor.
	 */
	private void setupCursor(DBCursor cursor) {
		if (readPreference!=null) {
			cursor.setReadPreference(readPreference);
		}
		if (firstDocument!=null) {
			cursor.skip(firstDocument);
		}
		if (maxDocuments!=null) {
			cursor.limit(maxDocuments);
		}
		if (batchSize!=null) {
			cursor.batchSize(batchSize);
		}
		if (hint!=null) {
			cursor.hint(hint);
		}
		if (snapShot!=null && snapShot==true) {
			cursor.snapshot();
		}
		if (comment!=null) {
			cursor.addSpecial("$comment", comment);
		}
		if (!specials.isEmpty()) {
			for (Entry<String, Object> special : specials.entrySet()) {
				cursor.addSpecial(special.getKey(), special.getValue());
			}
		}
		if (!sort.isEmpty()) {
			cursor.sort(getSortDBObject());
		}
		if (cursorVisitor!=null) {
			cursorVisitor.visit(cursor);
		}
	}

	/**
	 * Creates and returns the DBObject representing
	 * the sort for this query.
	 * @return the sort
	 */
	public DBObject getSortDBObject() {
		DBObject sortObj = new BasicDBObject();
		if (!sort.isEmpty()) {
			for (Entry<String, Integer> entry : sort.entrySet()) {
				sortObj.put(entry.getKey(), entry.getValue());
			}
		}
		return sortObj;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DaoQuery self() {
		return this;
	}

	/**
	 * Adds a sort.
	 * @param name the field
	 * @param direction the direction
	 * @return self
	 */
	public DaoQuery addSort(String name, Integer direction) {
		this.sort.put(name, direction);
		return self();
	}

	/**
	 * Adds a special.
	 * @param name the name
	 * @param special the special
	 * @return self
	 */
	public DaoQuery addSpecial(String name, Object special) {
		this.specials.put(name, special);
		return self();
	}

	/**
	 * @param sort the sort to set
	 */
	public DaoQuery setSort(Map<String, Integer> sort) {
		this.sort = sort;
		return self();
	}

	/**
	 * @param sort the sort to set
	 */
	@SuppressWarnings("unchecked")
	public DaoQuery setSort(DBObject sort) {
		this.sort.clear();
		this.sort.putAll(sort.toMap());
		return self();
	}

	/**
	 * @param specials the specials to set
	 */
	public void setSpecials(Map<String, Object> specials) {
		this.specials = specials;
	}

	/**
	 * @param firstDocument the firstDocument to set
	 */
	public DaoQuery setFirstDocument(Integer firstDocument) {
		this.firstDocument = firstDocument;
		return self();
	}

	/**
	 * @param maxDocuments the maxDocuments to set
	 */
	public DaoQuery setMaxDocuments(Integer maxDocuments) {
		this.maxDocuments = maxDocuments;
		return self();
	}

	/**
	 * @param batchSize the batchSize to set
	 */
	public DaoQuery setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return self();
	}

	/**
	 * @param hint the hint to set
	 */
	public DaoQuery setHint(String hint, int dir) {
		this.hint = new BasicDBObject(hint, dir);
		return self();
	}

	/**
	 * @param hint the hint to set
	 */
	public DaoQuery setHint(DBObject hint) {
		this.hint = hint;
		return self();
	}

	/**
	 * @param snapShot the snapShot to set
	 */
	public DaoQuery setSnapShot(Boolean snapShot) {
		this.snapShot = snapShot;
		return self();
	}

	/**
	 * @param snapShot the snapShot to set
	 */
	public DaoQuery setSnapShot() {
		setSnapShot(true);
		return self();
	}

	/**
	 * @param comment the comment to set
	 */
	public DaoQuery setComment(String comment) {
		this.comment = comment;
		return self();
	}

	/**
	 * @param collection the collection to set
	 */
	public DaoQuery setCollection(String collection) {
		this.collection = collection;
		return self();
	}

	/**
	 * @param mongoDao the mongoDao to set
	 */
	public DaoQuery setDB(DB db) {
		this.db = db;
		return self();
	}

	/**
	 * @param cursorVisitor the cursorVisitor to set
	 */
	public DaoQuery setCursorVisitor(CursorVisitor cursorVisitor) {
		this.cursorVisitor = cursorVisitor;
		return self();
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public DaoQuery setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return self();
	}

	/**
	 * @param readPreference the readPreference to set
	 */
	public DaoQuery setReadPreference(ReadPreference readPreference) {
		this.readPreference = readPreference;
		return self();
	}

	/**
	 * Returns the collection that this query is for.
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * Returns the {@link MongoDao} that created this query.
	 * @return the mongo dao
	 */
	public DB getDB() {
		return db;
	}

	/**
	 * Returns the {@link ObjectMapper}.
	 * @return the mongo dao
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

}
