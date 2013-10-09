package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.AbstractMongoDBIntegrationTest;
import com.googlecode.mjorm.ObjectIterator;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class StatementImplTest
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
	public void testExecute_DBCursor()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement("from people select *");
		DBCursor cursor = st.execute();
		assertNotNull(cursor);
		List<DBObject> objs = super.readAll(cursor);
		assertEquals(3, objs.size());

		// try again
		st = super.createStatement("FROM people WHERE firstName =~ /first[1|2]/ select *");
		cursor = st.execute();
		assertNotNull(cursor);
		objs = super.readAll(cursor);
		assertEquals(2, objs.size());
	}

	@Test
	public void testExecute_ObjectIterator()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement("from people select *");
		ObjectIterator<AnnotatedPerson> cursor = st.execute(AnnotatedPerson.class);
		assertNotNull(cursor);
		List<AnnotatedPerson> objs = cursor.readAll();
		assertEquals(3, objs.size());

		// try again
		st = super.createStatement("from people where firstName =~ /first[1|2]/ select *");
		cursor = st.execute(AnnotatedPerson.class);
		assertNotNull(cursor);
		objs = cursor.readAll();
		assertEquals(2, objs.size());
	}

	@Test
	public void testExecuteSingle_DBObject()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement("from people select *");
		DBObject obj = st.executeSingle();
		assertNotNull(obj);

		// try again
		st = super.createStatement("from people where firstName =~ /first[1|2]/ select *");
		obj = st.executeSingle();
		assertNotNull(obj);
	}

	@Test
	public void testExecuteSingle_Pojo()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement("from people select *");
		AnnotatedPerson obj = st.executeSingle(AnnotatedPerson.class);
		assertNotNull(obj);

		// try again
		st = super.createStatement("from people where firstName =~ /first[1|2]/ select *");
		obj = st.executeSingle(AnnotatedPerson.class);
		assertNotNull(obj);
	}

	@Test
	public void testExecuteUpdate()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement("from people where firstName='first1' update set firstName='new first name'");
		st.executeUpdate();

		// try again
		st = super.createStatement("from people where firstName='new first name' select *");
		AnnotatedPerson obj = st.executeSingle(AnnotatedPerson.class);
		assertNotNull(obj);
		assertEquals("new first name", obj.getFirstName());
	}

	@Test
	public void testExecute_DBCursor_WithParams()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// try again
		StatementImpl st = super.createStatement("from people where firstName =~ :fn select *");
		st.setParameter("fn", Pattern.compile("first[1|2]"));
		DBCursor cursor = st.execute();
		assertNotNull(cursor);
		List<DBObject> objs = super.readAll(cursor);
		assertEquals(2, objs.size());

		// try again
		st = super.createStatement("from people where firstName =~ ? select *");
		st.setParameter(0, Pattern.compile("first[1|2]"));
		cursor = st.execute();
		assertNotNull(cursor);
		objs = super.readAll(cursor);
		assertEquals(2, objs.size());
	}

	@Test
	public void testExecute_DBCursor_WithParams_ReUse()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// try again
		StatementImpl st = super.createStatement("from people where firstName=:firstName select *");
		st.setParameter("firstName", "first0");
		DBObject obj = st.executeSingle();
		assertNotNull(obj);
		
		st.setParameter("firstName", "first1");
		obj = st.executeSingle();
		assertNotNull(obj);
		
		st.setParameter("firstName", "first2");
		obj = st.executeSingle();
		assertNotNull(obj);
		
		st.setParameter("firstName", "first3");
		obj = st.executeSingle();
		assertNull(obj);

	}

	@Test
	public void testExecuteUpdate_WithParams()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement("from people where firstName=:oldFirst update set firstName=:newFirst");
		st.setParameter("oldFirst", "first1");
		st.setParameter("newFirst", "new first name");
		st.executeUpdate();

		// try again
		st = super.createStatement("from people where firstName=:newFirst select *");
		st.setParameter("newFirst", "new first name");
		AnnotatedPerson obj = st.executeSingle(AnnotatedPerson.class);
		assertNotNull(obj);
		assertEquals("new first name", obj.getFirstName());
	}

	@Test
	public void testExecuteUpdate_WithFunction()
		throws Exception{

		// add 3 people
		super.addPeople(3);

		// create statement
		StatementImpl st = super.createStatement(
			"from people where firstName=:firstName update set aDate=date('1979-07-02 01:02:03') set aNowDate=now() set testing='line\\nbreak'");
		st.setParameter("firstName", "first1");
		st.executeUpdate();

		// try again
		st = super.createStatement("from people where firstName=:firstName select *");
		st.setParameter("firstName", "first1");
		DBObject obj = st.executeSingle();
		assertNotNull(obj);
		assertNotNull(obj.get("aDate"));
		assertNotNull(obj.get("aNowDate"));
		assertNotNull(obj.get("testing"));
		assertEquals("line\nbreak", obj.get("testing"));
		Date date = Date.class.cast(obj.get("aDate"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(1979, cal.get(Calendar.YEAR));
		assertEquals(Calendar.JULY, cal.get(Calendar.MONTH));
		assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));
	}

}
