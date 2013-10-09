package com.googlecode.mjorm.convert.converters;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.convert.JavaType;

public class NumberTypeConverterTest {

	private NumberTypeConverter conv;

	@Before
	public void setUp()
		throws Exception {
		conv = new NumberTypeConverter();
	}

	@After
	public void tearDown()
		throws Exception {
		conv = null;
	}

	@Test
	public void testCanConvert() {
		assertTrue(conv.canConvert(Number.class, Number.class));
		assertTrue(conv.canConvert(Float.class, Integer.class));
		assertFalse(conv.canConvert(Float.class, Boolean.class));
		assertFalse(conv.canConvert(String.class, Boolean.class));
	}

	@Test
	public void testConvert()
		throws Exception {
		assertEquals((byte)1, conv.convert(1.0f, JavaType.fromType(Byte.class), null, null ));
		assertEquals((byte)1, conv.convert(1.0d, JavaType.fromType(Byte.class), null, null ));
		assertEquals((byte)1, conv.convert((int)1, JavaType.fromType(Byte.class), null, null ));
		assertEquals((byte)1, conv.convert((short)1, JavaType.fromType(Byte.class), null, null ));
		
		assertEquals((short)1, conv.convert(1.0f, JavaType.fromType(Short.class), null, null ));
		assertEquals((short)1, conv.convert(1.0d, JavaType.fromType(Short.class), null, null ));
		assertEquals((short)1, conv.convert((int)1, JavaType.fromType(Short.class), null, null ));
		assertEquals((short)1, conv.convert((short)1, JavaType.fromType(Short.class), null, null ));
		
		assertEquals(1, conv.convert(1.0f, JavaType.fromType(Integer.class), null, null ));
		assertEquals(1, conv.convert(1.0d, JavaType.fromType(Integer.class), null, null ));
		assertEquals(1, conv.convert((int)1, JavaType.fromType(Integer.class), null, null ));
		assertEquals(1, conv.convert((short)1, JavaType.fromType(Integer.class), null, null ));
		
		assertEquals(1L, conv.convert(1.0f, JavaType.fromType(Long.class), null, null ));
		assertEquals(1L, conv.convert(1.0d, JavaType.fromType(Long.class), null, null ));
		assertEquals(1L, conv.convert((int)1, JavaType.fromType(Long.class), null, null ));
		assertEquals(1L, conv.convert((short)1, JavaType.fromType(Long.class), null, null ));
		
		assertEquals(1.0f, conv.convert(1.0f, JavaType.fromType(Float.class), null, null ));
		assertEquals(1.0f, conv.convert(1.0d, JavaType.fromType(Float.class), null, null ));
		assertEquals(1.0f, conv.convert((int)1.0, JavaType.fromType(Float.class), null, null ));
		assertEquals(1.0f, conv.convert((short)1.0, JavaType.fromType(Float.class), null, null ));
		
		assertEquals(1.0d, conv.convert(1.0f, JavaType.fromType(Double.class), null, null ));
		assertEquals(1.0d, conv.convert(1.0d, JavaType.fromType(Double.class), null, null ));
		assertEquals(1.0d, conv.convert((int)1, JavaType.fromType(Double.class), null, null ));
		assertEquals(1.0d, conv.convert((short)1, JavaType.fromType(Double.class), null, null ));
		
		assertEquals(BigDecimal.valueOf(1.0), conv.convert(1f, JavaType.fromType(BigDecimal.class), null, null ));
		assertEquals(BigDecimal.valueOf(1.0), conv.convert(1d, JavaType.fromType(BigDecimal.class), null, null ));
		assertEquals(BigDecimal.valueOf(1.0), conv.convert((int)1, JavaType.fromType(BigDecimal.class), null, null ));
		assertEquals(BigDecimal.valueOf(1.0), conv.convert((short)1, JavaType.fromType(BigDecimal.class), null, null ));
	}

}
