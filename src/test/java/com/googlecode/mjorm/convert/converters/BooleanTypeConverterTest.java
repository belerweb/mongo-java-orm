package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class BooleanTypeConverterTest {

	private  BooleanTypeConverter conv;

	@Before
	public void setUp()
		throws Exception {
		conv = new  BooleanTypeConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		conv = null;
	}

	@Test
	public void testCanConvert() {
		assertTrue(conv.canConvert(Number.class, Boolean.class));
		assertTrue(conv.canConvert(Character.class, Boolean.class));
		assertTrue(conv.canConvert(String.class, Boolean.class));
	}

	@Test
	public void testConvert()
		throws Exception {
		assertEquals(Boolean.valueOf(true), conv.convert(1, JavaType.fromType(Number.class), null, null));
		assertEquals(Boolean.valueOf(true), conv.convert("true", JavaType.fromType(String.class), null, null));
		assertEquals(Boolean.valueOf(true), conv.convert((char)1, JavaType.fromType(Character.class), null, null));
		assertEquals(Boolean.valueOf(false), conv.convert((char)0, JavaType.fromType(Character.class), null, null));
	}

}
