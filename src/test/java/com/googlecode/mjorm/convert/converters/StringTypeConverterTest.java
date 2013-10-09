package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class StringTypeConverterTest {

	private StringTypeConverter conv = null;

	@Before
	public void setUp()
		throws Exception {
		conv = new StringTypeConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		conv = null;
	}

	@Test
	public void testCanConvert() {
		assertTrue(conv.canConvert(null, String.class));
		assertFalse(conv.canConvert(null, Boolean.class));
	}

	@Test
	public void testConvert()
		throws Exception {
		assertEquals("true", conv.convert(true, JavaType.fromType(String.class), null, null));
		assertEquals("1", conv.convert(1, JavaType.fromType(String.class), null, null));
		assertEquals("1.0", conv.convert(1.0f, JavaType.fromType(String.class), null, null));
	}

}
