package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class CharacterTypeConverterTest {

	private CharacterTypeConverter conv = null;

	@Before
	public void setUp()
		throws Exception {
		conv = new CharacterTypeConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		conv = null;
	}

	@Test
	public void testCanConvert() {
		assertTrue(conv.canConvert(Number.class, Character.class));
		assertTrue(conv.canConvert(String.class, Character.class));
		assertTrue(conv.canConvert(Boolean.class, Character.class));
		assertFalse(conv.canConvert(Object.class, Character.class));
	}

	@Test
	public void testConvert()
		throws Exception {
		assertEquals(Character.valueOf((char)1), conv.convert(1, JavaType.fromType(Number.class), null, null));
		assertEquals(Character.valueOf('1'), conv.convert("1", JavaType.fromType(String.class), null, null));
		assertEquals(Character.valueOf((char)1), conv.convert(true, JavaType.fromType(Boolean.class), null, null));
		assertEquals(Character.valueOf((char)0), conv.convert(false, JavaType.fromType(Boolean.class), null, null));
	}

}
