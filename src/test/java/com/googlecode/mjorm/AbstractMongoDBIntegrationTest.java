package com.googlecode.mjorm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.RecognitionException;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.googlecode.mjorm.mql.AnnotatedAddress;
import com.googlecode.mjorm.mql.InterpreterFactory;
import com.googlecode.mjorm.mql.InterpreterImpl;
import com.googlecode.mjorm.mql.AnnotatedPerson;
import com.googlecode.mjorm.mql.StatementImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public abstract class AbstractMongoDBIntegrationTest {

	protected final static String DB_NAME = "mjorm_test_db";

	protected boolean canTest;
	protected InterpreterImpl interpreter;
	protected Mongo mongo;
	protected DB db;
	protected ObjectMapper objectMapper;
	protected DBCollection collection;

	protected void setUpDb()
		throws Exception {
		
		// connect to mongo
		// sometimes this doesn't work on the first
		// attempt for wahtever reason - so we give it 10 attempts
		for (int i=1; true; i++) {
			try {
				mongo = new Mongo(new MongoURI("mongodb://127.0.0.1"));
				mongo.getDatabaseNames();
				db = mongo.getDB(DB_NAME);
				canTest = true;
				break;
			} catch(Throwable t) {
				if (i>=10) {
					System.err.println("Unable to run MongoDB integration tests: "+t.getMessage());
					t.printStackTrace();
					canTest = false;
					return;
				}
				System.err.print("Connection attempt #"+i+" of 10");
			}
		}
	
		for (String c : db.getCollectionNames()) {
			db.getCollection(c).drop();
		}
		db.createCollection("people", new BasicDBObject());
		collection = db.getCollection("people");
	}

	protected void setupObjectMapper()
		throws Exception {
		
		// create objectMapper
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		mapper.addClass(AnnotatedPerson.class);
		mapper.addClass(AnnotatedAddress.class);
		objectMapper = mapper;
	}

	protected void setupInterpreter()
		throws Exception {
	
		// create interpreter
		interpreter = (InterpreterImpl)InterpreterFactory
			.getDefaultInstance().create(db, objectMapper);
	}

	protected void tearDown()
		throws Exception {
		if (!canTest) { return; }
		interpreter = null;
		objectMapper = null;
		if (mongo!=null) {
			mongo.dropDatabase(DB_NAME);
			mongo.close();
			mongo = null;
		}
	}

	protected StatementImpl createStatement(String statement)
		throws IOException,
		RecognitionException {
		return new StatementImpl(
			new ByteArrayInputStream(statement.getBytes()), db, objectMapper);
	}

	protected void addPerson(String firstName, String lastName, String street,
		String city, String state, String zip, int num) {
			DBObject object = BasicDBObjectBuilder.start()
				.add("firstName", firstName)
				.add("lastName", lastName)
				.add("numbers", new Object[] {1,2,3})
				.add("num", num)
				.push("address")
					.add("street", street)
					.add("city", city)
					.add("state", state)
					.add("zipCode", zip)
					.pop()
				.get();
			collection.insert(object);
		}

	protected void addPeople(int num) {
		for (int i=0; i<num; i++) {
			addPerson(
				"first"+i, "last"+i, "street"+i, "city"+i, "state"+i, "zip"+i, i);
		}
	}

	protected InputStream rs(String resource) throws IOException {
		return getClass().getResourceAsStream(resource);
	}

	protected InputStream ips(String command) {
		return new ByteArrayInputStream(command.getBytes());
	}

	protected List<DBObject> readAll(DBCursor cursor) {
		List<DBObject> ret = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			ret.add(cursor.next());
		}
		return ret;
	}

}