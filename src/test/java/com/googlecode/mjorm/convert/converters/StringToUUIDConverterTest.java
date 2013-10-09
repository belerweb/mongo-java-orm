package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class StringToUUIDConverterTest {

	private StringToUUIDConverter converter;

	@Before
	public void setUp()
		throws Exception {
		converter = new StringToUUIDConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		converter = null;
	}

	@Test
	public final void testCanConvert() {
		assertTrue(converter.canConvert(byte[].class, UUID.class));
		assertTrue(converter.canConvert(String.class, UUID.class));
		assertFalse(converter.canConvert(Object.class, UUID.class));
		assertFalse(converter.canConvert(byte[].class, Object.class));
		assertFalse(converter.canConvert(String.class, Object.class));
	}

	@Test
	public final void testConvert()
		throws Exception {
		UUID uuid = UUID.nameUUIDFromBytes("test".getBytes());
		assertEquals(uuid, converter.convert("test".getBytes(), JavaType.fromType(byte[].class), null, null));
		assertEquals(uuid, converter.convert("test".getBytes(), JavaType.fromType(Byte[].class), null, null));
		assertEquals(uuid, converter.convert(uuid.toString(), JavaType.fromType(String.class), null, null));
	}

}
