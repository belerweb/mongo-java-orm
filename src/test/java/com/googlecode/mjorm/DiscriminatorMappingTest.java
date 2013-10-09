package com.googlecode.mjorm;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class DiscriminatorMappingTest {

	private AnnotationsDescriptorObjectMapper annotationsMapper;
	private XmlDescriptorObjectMapper xmlMapper;

	@Before
	public void setUp()
		throws Exception {
		annotationsMapper = new AnnotationsDescriptorObjectMapper();
		annotationsMapper.addClass(DiscriminatorTestObject.class);
		xmlMapper = new XmlDescriptorObjectMapper();
		xmlMapper.addXmlObjectDescriptor(res("/com/googlecode/mjorm/DiscriminatorTestObject.mongo.xml"));
	}

	@After
	public void tearDown()
		throws Exception {
	}

	private InputStream res(String path) {
		return getClass().getResourceAsStream(path);
	}

	@Test
	public void testAnnotations() {

		// create test objects
		BasicDBObject one = (BasicDBObject)BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "1")
			.add("disc", "subClassOne")
			.add("one", "it is one")
			.get();

		BasicDBObject two = (BasicDBObject)BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "2")
			.add("disc", "subClassTwo")
			.add("two", "it is two")
			.get();

		BasicDBObject three = (BasicDBObject)BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "3")
			.get();

		DiscriminatorTestObject obj = annotationsMapper.map(one, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassOne.class, obj.getClass());
		assertEquals("1", obj.getName());
		assertEquals("it is one", TestObjectSubClassOne.class.cast(obj).getOne());

		obj = annotationsMapper.map(two, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassTwo.class, obj.getClass());
		assertEquals("2", obj.getName());
		assertEquals("it is two", TestObjectSubClassTwo.class.cast(obj).getTwo());

		obj = annotationsMapper.map(three, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(DiscriminatorTestObject.class, obj.getClass());
		assertEquals("3", obj.getName());
	}

	@Test
	public void testXML() {

		// create test objects
		DBObject one = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "1")
			.add("disc", "subClassOne")
			.add("one", "it is one")
			.get();

		DBObject two = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "2")
			.add("disc", "subClassTwo")
			.add("two", "it is two")
			.get();

		DBObject three = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "3")
			.get();

		DiscriminatorTestObject obj = xmlMapper.map(one, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassOne.class, obj.getClass());
		assertEquals("1", obj.getName());
		assertEquals("it is one", TestObjectSubClassOne.class.cast(obj).getOne());

		obj = xmlMapper.map(two, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassTwo.class, obj.getClass());
		assertEquals("2", obj.getName());
		assertEquals("it is two", TestObjectSubClassTwo.class.cast(obj).getTwo());

		obj = xmlMapper.map(three, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(DiscriminatorTestObject.class, obj.getClass());
		assertEquals("3", obj.getName());
	}

}
