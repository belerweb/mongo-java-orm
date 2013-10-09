package com.googlecode.mjorm.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.googlecode.mjorm.MapReduceConfiguration;
import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.MongoDaoImpl;
import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * DAO Support class for MongoDB and spring.
 */
public class MongoDBDaoSupport
	extends DaoSupport
	implements InitializingBean,
	BeanFactoryAware {

	private MongoDao mongoDao;
	private BeanFactory beanFactory;
	private ObjectMapper objectMapper;
	private Mongo mongo;
	private DB db;
	private String dbName;
	private String username;
	private String password;
	private Map<String, MapReduceConfiguration> mapReduceConfigs
		= new HashMap<String, MapReduceConfiguration>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkDaoConfig() {

		// make sure we have a mapper
		if (objectMapper==null
			&& ListableBeanFactory.class.isInstance(beanFactory)) {
			objectMapper = BeanFactoryUtils.beanOfType(
				ListableBeanFactory.class.cast(beanFactory), ObjectMapper.class);
		}
		Assert.notNull(objectMapper, "an ObjectMapper is required");

		// get the DB
		if (mongo==null
			&& ListableBeanFactory.class.isInstance(beanFactory)) {
			mongo = BeanFactoryUtils.beanOfType(
				ListableBeanFactory.class.cast(beanFactory), Mongo.class);
		}
		Assert.notNull(mongo, "a Mongo connection is required");

		// get the db
		if (db==null && StringUtils.hasText(dbName)) {
			db = mongo.getDB(dbName);
		}
		Assert.notNull(db, "a mongo db or db name is required");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initDao()
		throws Exception {

		// authenticate if needed
		if (StringUtils.hasText(username)
			&& StringUtils.hasText(password)) {
			db.authenticate(username, password.toCharArray());
		}

		// create the DAO if needed
		if (mongoDao==null) {
			mongoDao = new MongoDaoImpl(db, objectMapper);
		}

		// ensure indexes
		ensureIndexes();
	}

	/**
	 * Called durring the initialization phase to enable
	 * the DAO to ensure mongo indexes.
	 */
	protected void ensureIndexes() {
		// no-op
	}

	/**
	 * Loads a {@link MapReduceConfiguration}.
	 * @param classPath the resource name
	 * @return the {@link MapReduceConfiguration}
	 */
	protected MapReduceConfiguration getMapReduceConfiguration(String classPath) {
		if (!mapReduceConfigs.containsKey(classPath)) {
			try {
				MapReduceConfiguration config = MapReduceConfiguration.create(
					getClass().getResourceAsStream(classPath));
				mapReduceConfigs.put(classPath, config);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return mapReduceConfigs.get(classPath);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns a {@link MongoDao}.
	 * @return the {@link MongoDao}
	 */
	public MongoDao getMongoDao() {
		return mongoDao;
	}

	/**
	 * Gets a {@link DBCollection} from the {@link DB}.
	 * @param name the name of the collection
	 * @return the name
	 */
	public DBCollection getCollection(String name) {
		return getDb().getCollection(name);
	}

	/**
	 * @return the db
	 */
	public DB getDb() {
		return db;
	}

	/**
	 * @return the mongo
	 */
	public Mongo getMongo() {
		return mongo;
	}

	/**
	 * @param mongo the mongo to set
	 */
	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}

	/**
	 * @param DB_NAME the DB_NAME to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param mongoDao the mongoDao to set
	 */
	public void setMongoDao(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(DB db) {
		this.db = db;
	}

	/**
	 * @return the objectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

}
