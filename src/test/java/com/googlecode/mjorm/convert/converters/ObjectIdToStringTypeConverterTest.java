package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class ObjectIdToStringTypeConverterTest {

	private ObjectIdToStringTypeConverter converter;

	@Before
	public void setUp()
		throws Exception {
		converter = new ObjectIdToStringTypeConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		converter = null;
	}

	@Test
	public void testCanConvert() {
		assertTrue(converter.canConvert(ObjectId.class, String.class));
		assertFalse(converter.canConvert(ObjectId.class, Boolean.class));
		assertFalse(converter.canConvert(Boolean.class, String.class));
	}

	@Test
	public void testConvert()
		throws Exception {
		ObjectId id = new ObjectId();
		assertEquals(id.toStringMongod(), converter.convert(id, JavaType.fromType(String.class), null, null));
	}

}
