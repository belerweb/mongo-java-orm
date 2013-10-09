package com.googlecode.mjorm;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MapReduceConfigurationTest {

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}

	@Test
	public void testCreate1()
		throws Exception {
		MapReduceConfiguration config = MapReduceConfiguration.create(
			getClass().getResourceAsStream("/com/googlecode/mjorm/mapReduce1.xml"));
		assertNotNull(config);
		assertNotNull(config.getMapFunction());
		assertNotNull(config.getReduceFunction());
		assertNotNull(config.getFinalizeFunction());
		assertEquals("mapFunction", config.getMapFunction());
		assertEquals("reduceFunction", config.getReduceFunction());
		assertEquals("finalizeFunction", config.getFinalizeFunction());
	}

	@Test
	public void testCreate2()
		throws Exception {
		MapReduceConfiguration config = MapReduceConfiguration.create(
			getClass().getResourceAsStream("/com/googlecode/mjorm/mapReduce2.xml"));
		assertNotNull(config);
		assertNotNull(config.getMapFunction());
		assertNotNull(config.getReduceFunction());
		assertNull(config.getFinalizeFunction());
		assertEquals("mapFunction", config.getMapFunction());
		assertEquals("reduceFunction", config.getReduceFunction());
	}

	@Test
	public void testCreate3()
		throws Exception {
		MapReduceConfiguration config = MapReduceConfiguration.create(
			getClass().getResourceAsStream("/com/googlecode/mjorm/mapReduce3.xml"));
		assertNotNull(config);
		assertNull(config.getMapFunction());
		assertNull(config.getReduceFunction());
		assertNull(config.getFinalizeFunction());
	}

}
