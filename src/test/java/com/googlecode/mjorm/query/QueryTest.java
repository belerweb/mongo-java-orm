package com.googlecode.mjorm.query;

import static org.junit.Assert.*;
import static com.googlecode.mjorm.query.criteria.Criteria.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.DescriptorObjectMapper;
import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.query.criteria.TypeCriterion.Type;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class QueryTest {

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		mapper = new DescriptorObjectMapper();
	}

	@After
	public void tearDown() {
		mapper = new DescriptorObjectMapper();
	}

	@Test
	public void testEmpty() {
		Query queryCriterion = new Query();
		assertEquals(new BasicDBObject(), queryCriterion.toQueryObject(mapper));
	}

	@Test
	public void testMultipleCriteriaForProperty() {
		Query queryCriterion = new Query()
			.gt("value", 10)
			.lt("value", 20);
		DBObject obj = BasicDBObjectBuilder.start()
			.add("value", BasicDBObjectBuilder.start()
				.add("$gt", 10)
				.add("$lt", 20)
				.get())
			.get();

		assertEquals(obj, queryCriterion.toQueryObject(mapper));
	}

	@Test(expected=IllegalStateException.class)
	public void testMultipleCriteriaForProperty_WithError() {
		Query queryCriterion = new Query()
			.gt("value", 10)
			.lt("value", 20)
			.eq("value", 10);

		queryCriterion.toQueryObject(mapper);
	}

	@Test
	public void testStack() {
		Query queryCriterion = new Query()
			.eq("a", 1)
			.eq("c", 1)
			.push("level2")
				.eq("b", 1)
				.push("level3")
					.eq("d", 1)
					.eq("c", 1)
					.pop()
				.eq("e", 1)
				.pop()
			.eq("f", 1)
			.eq("what.what", "in the butt")
		;
		
		DBObject obj = BasicDBObjectBuilder.start()
			.add("a", 1)
			.add("c", 1)
			.push("level2")
				.add("b", 1)
				.push("level3")
					.add("d", 1)
					.add("c", 1)
					.pop()
				.add("e", 1)
				.pop()
			.add("f", 1)
			.add("what.what", "in the butt")
			.get();

		assertEquals(obj, queryCriterion.toQueryObject(mapper));
	}

	@Test
	public void testFull() {
		Query query1 = new Query();
		query1
			.add("eq", eq("eqtest"))
			.add("gt", gt(1))
			.add("gte", gte(2))
			.add("lt", lt(3))
			.add("lte", lte(4))
			.add("between", between(1, 2))
			.add("ne", ne("netest"))
			.add("in1", in(1))
			.add("in12", in(1, 2))
			.add("inList", in(Arrays.asList(1, 2, 3)))
			.add("nin1", nin(1))
			.add("nin12", nin(1, 2))
			.add("ninList", nin(Arrays.asList(1, 2, 3)))
			.add("all1", all(1))
			.add("all12", all(1, 2))
			.add("allList", all(Arrays.asList(1, 2, 3)))
			.add("existsFalse", exists(false))
			.add("existsTrue", exists(true))
			.add("mod12", mod(1, 2))
			.add("regex", regex("/[A-z]/"))
			.add("size10", size(10))
			.add("type1", type(1))
			.add("typeString", type(Type.STRING))
			.add("elemMatch", elemMatch(new Query().eq("x", 1).eq("y", 2)))
			.add(not("not", eq("noteqtest")))
			.add("near1", near(1, 2))
			.add("near2", near(1, 2, 3))
			.add("within_box", within(1, 2, 3, 4))
			.add("within_circle", within(1, 2, 3))
			.add("within_poly", within(new Number[][] {  new Number[]{1, 2}, new Number[]{3, 4}, new Number[]{5, 6}}))
			
			.or(new QueryGroup()
				.add(new Query().add("or1", eq("or1test")))
				.add(new Query().add("or2", eq("or2test")))
			)
			.nor(new QueryGroup()
				.add(new Query().add("nor1", eq("nor1test")))
				.add(new Query().add("nor2", eq("nor2test")))
			)
			.and(new QueryGroup()
				.add(new Query().add("and1", eq("and1test")))
				.add(new Query().add("and2", eq("and2test")))
			)
		;
		
		Query query2 = new Query();
		query2
			.eq("eq", "eqtest")
			.gt("gt", 1)
			.gte("gte", 2)
			.lt("lt", 3)
			.lte("lte", 4)
			.between("between", 1, 2)
			.ne("ne", "netest")
			.in("in1", 1)
			.in("in12", 1, 2)
			.in("inList", Arrays.asList(1, 2, 3))
			.nin("nin1", 1)
			.nin("nin12", 1, 2)
			.nin("ninList", Arrays.asList(1, 2, 3))
			.all("all1", 1)
			.all("all12", 1, 2)
			.all("allList", Arrays.asList(1, 2, 3))
			.exists("existsFalse", false)
			.exists("existsTrue", true)
			.mod("mod12", 1, 2)
			.regex("regex", "/[A-z]/")
			.size("size10", 10)
			.type("type1", 1)
			.type("typeString", Type.STRING)
			.elemMatch("elemMatch", new Query().eq("x", 1).eq("y", 2))
			.not("not", eq("noteqtest"))
			.near("near1", 1, 2)
			.near("near2", 1, 2, 3)
			.within("within_box", 1, 2, 3, 4)
			.within("within_circle", 1, 2, 3)
			.within("within_poly", new Number[][] {  new Number[]{1, 2}, new Number[]{3, 4}, new Number[]{5, 6}})
			.or(new QueryGroup()
				.add(new Query().add("or1", eq("or1test")))
				.add(new Query().add("or2", eq("or2test")))
			)
			.nor(new QueryGroup()
				.add(new Query().add("nor1", eq("nor1test")))
				.add(new Query().add("nor2", eq("nor2test")))
			)
			.and(new QueryGroup()
				.add(new Query().add("and1", eq("and1test")))
				.add(new Query().add("and2", eq("and2test")))
			)
		;

		assertEquals(query1.toQueryObject(mapper).toString(), query2.toQueryObject(mapper).toString());
	}

}
