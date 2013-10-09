package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.Tree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.AbstractMongoDBIntegrationTest;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class InterpreterTest
	extends AbstractMongoDBIntegrationTest {

	@Before
	public void setUp()
		throws Exception {
		super.setUpDb();
		super.setupObjectMapper();
		super.setupInterpreter();
	}

	@After
	public void tearDown()
		throws Exception {
		super.tearDown();
	}

	@Test
	public void testSelect_withLimit()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips("from people select * limit 0, 2"));
	
		// interpret
		InterpreterResult res = interpreter.interpret(tree).get(0);

		// verify
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(2, people.size());
		assertEquals("first0", people.get(0).get("firstName"));
		assertEquals("first1", people.get(1).get("firstName"));

	}

	@Test
	public void testSelect_withFields()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips("from people select firstName"));
	
		// interpret
		InterpreterResult res = interpreter.interpret(tree).get(0);

		// verify
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(10, people.size());
		for (int i=0; i<people.size(); i++) {
			assertEquals(2, people.get(i).keySet().size());
			assertNotNull(people.get(i).get("_id"));
			assertNotNull(people.get(i).get("firstName"));
		}

	}

	@Test
	public void testUpdate()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// get value
		Tree tree = interpreter.compile(ips(
			"from people where firstName='first1' select *"));
		InterpreterResult res = interpreter.interpret(tree).get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("first1", people.get(0).get("firstName"));

		// update it
		tree = interpreter.compile(ips(
			"from people where firstName='first1' update "
			+"set firstName='new first name' rename lastName somethingElse"));
		res = interpreter.interpret(tree).get(0);

		// get value
		tree = interpreter.compile(ips(
			"from people where firstName='new first name' select *"));
		res = interpreter.interpret(tree).get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("new first name", people.get(0).get("firstName"));
		assertEquals("last1", people.get(0).get("somethingElse"));
		
	}

	@Test
	public void testUpdateMultipleCommands()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// get value
		StringBuilder buff = new StringBuilder();
		for (int i=0; i<100; i++) {
			buff.append("from people where firstName='first1' find and modify add to set numbers "+i+" select *; ");
		}
		Tree tree = interpreter.compile(ips(buff.toString()));
		List<InterpreterResult> res = interpreter.interpret(tree);
		assertNotNull(res);
		assertEquals(100, res.size());
		List<Integer> expect = new ArrayList<Integer>();
		expect.add(1);
		expect.add(2);
		expect.add(3);
		for (int i=0; i<100; i++) {
			DBObject obj = res.get(i).getObject();
			if (i<1 || i>3) { // 1 through 3 were already in it
				expect.add(i);
			}
			assertNotNull(obj);
			assertNotNull(obj.get("numbers"));
			BasicDBList numbers = BasicDBList.class.cast(obj.get("numbers"));
			assertArrayEquals(expect.toArray(new Object[0]), numbers.toArray(new Object[0]));
		}
		
	}

	@Test
	public void testUpdateWithNamedParams()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips(
			"from people where firstName=:firstName select *"));
		
		
		// interpret
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("firstName", "first1");
		InterpreterResult res = interpreter.interpret(tree, params).get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("first1", people.get(0).get("firstName"));

		// update it
		tree = interpreter.compile(ips(
			"from people where firstName=:firstName update "
			+"set firstName=:newFirstName rename lastName somethingElse"));
		params.clear();
		params.put("firstName", "first1");
		params.put("newFirstName", "new first name");
		res = interpreter.interpret(tree, params).get(0);

		// get value
		tree = interpreter.compile(ips(
			"from people where firstName=:newFirstName select *"));
		params.clear();
		params.put("newFirstName", "new first name");
		res = interpreter.interpret(tree, params).get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("new first name", people.get(0).get("firstName"));
		assertEquals("last1", people.get(0).get("somethingElse"));
		
	}

	@Test
	public void testUpdateWithIndexedParams()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips(
			"from people where firstName=? select *"));
		
		
		// interpret
		InterpreterResult res = interpreter.interpret(tree, "first1").get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("first1", people.get(0).get("firstName"));

		// update it
		tree = interpreter.compile(ips(
			"from people where firstName=? update "
			+"set firstName=? rename lastName somethingElse"));
		res = interpreter.interpret(tree, "first1", "new first name").get(0);

		// get value
		tree = interpreter.compile(ips(
			"from people where firstName=? select *"));
		res = interpreter.interpret(tree, "new first name").get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("new first name", people.get(0).get("firstName"));
		assertEquals("last1", people.get(0).get("somethingElse"));
		
	}

	@Test
	public void testPolyFunction()
		throws Exception {
		if (!canTest) { return; }

		// compile
		Tree tree = interpreter.compile(ips(
			"from people where whatever within_polygon([1,2], [3,4], [5,6]) select *"));
		
		// interpret
		InterpreterResult res = interpreter.interpret(tree, "first1").get(0);
		assertNotNull(res);
		
	}

}
