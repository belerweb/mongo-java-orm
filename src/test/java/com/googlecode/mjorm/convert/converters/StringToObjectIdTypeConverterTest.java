package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class StringToObjectIdTypeConverterTest {

	private StringToObjectIdTypeConverter converter;

	@Before
	public void setUp()
		throws Exception {
		converter = new StringToObjectIdTypeConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		converter = null;
	}

	@Test
	public void testCanConvert() {
		assertTrue(converter.canConvert(String.class, ObjectId.class));
		assertFalse(converter.canConvert(ObjectId.class, Boolean.class));
		assertFalse(converter.canConvert(Boolean.class, String.class));
	}

	@Test
	public void testConvert()
		throws Exception {
		String id = new ObjectId().toStringMongod();
		assertEquals(new ObjectId(id), converter.convert(id, JavaType.fromType(ObjectId.class), null, null));
	}

}
