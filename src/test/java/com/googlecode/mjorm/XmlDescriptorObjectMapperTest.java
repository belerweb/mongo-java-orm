package com.googlecode.mjorm;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class XmlDescriptorObjectMapperTest {

	private XmlDescriptorObjectMapper mapper;

	@Before
	public void setUp()
		throws Exception {
		mapper = new XmlDescriptorObjectMapper();
	}

	@After
	public void tearDown()
		throws Exception {
		mapper = null;
	}

	private InputStream res(String path) {
		return getClass().getResourceAsStream(path);
	}

	private void addMapping(String path) 
		throws Exception {
		mapper.addXmlObjectDescriptor(res(path));
	}

	@Test
	public void testConvertToAndFromDBObject()
		throws Exception {
		addMapping("/com/googlecode/mjorm/Address.mongo.xml");
		addMapping("/com/googlecode/mjorm/City.mongo.xml");
		addMapping("/com/googlecode/mjorm/Person.mongo.xml");
		addMapping("/com/googlecode/mjorm/State.mongo.xml");
		addMapping("/com/googlecode/mjorm/SuperDuperOverride.mongo.xml");

		City city = new City();
		city.setName("city name");
		city.setLat(new Float("123.456"));
		city.setLon(new Float("789.101"));

		Address address = new Address();
		address.setCity(city);
		address.setStreetName("street name");
		address.setStreetNumber(2435L);
		
		State state = new State();
		state.setName("state name");
		state.setCities(new HashSet<City>());
		state.getCities().add(city);

		DBObject cityDbObject = mapper.unmap(city);
		assertNotNull(cityDbObject);
		assertEquals(city.getName(), cityDbObject.get("name"));
		assertEquals(city.getLat(), cityDbObject.get("lat"));
		assertEquals(city.getLon(), cityDbObject.get("lon"));

		DBObject addressDbObject = mapper.unmap(address);
		assertNotNull(addressDbObject);
		DBObject addressCityDbObject = (DBObject)addressDbObject.get("city");
		assertNotNull(addressCityDbObject);
		assertEquals(address.getStreetName(), addressDbObject.get("streetName"));
		assertEquals(address.getStreetNumber(), addressDbObject.get("streetNumber"));
		assertEquals(city.getName(), addressCityDbObject.get("name"));
		assertEquals(city.getLat(), addressCityDbObject.get("lat"));
		assertEquals(city.getLon(), addressCityDbObject.get("lon"));

		DBObject stateDbObject = mapper.unmap(state);
		assertNotNull(stateDbObject);
		BasicDBList stateCityDbList = (BasicDBList)stateDbObject.get("cities");
		assertNotNull(stateCityDbList);
		DBObject stateCityDbListObject = (DBObject)stateCityDbList.get(0);
		assertNotNull(stateCityDbList);
		assertEquals(state.getName(), stateDbObject.get("name"));
		assertEquals(city.getName(), stateCityDbListObject.get("name"));
		assertEquals(city.getLat(), stateCityDbListObject.get("lat"));
		assertEquals(city.getLon(), stateCityDbListObject.get("lon"));

		City transformedCity = mapper.map(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertEquals(city, transformedCity);

		Address transformedAddress = mapper.map(addressDbObject, Address.class);
		assertNotNull(transformedAddress);
		assertEquals(address, transformedAddress);

		State transformedState = mapper.map(stateDbObject, State.class);
		assertNotNull(transformedState);
		assertEquals(state, transformedState);
	}

	@Test
	public void testConvertToAndFromDBObject_SuperDuper()
		throws Exception {
		addMapping("/com/googlecode/mjorm/Address.mongo.xml");
		addMapping("/com/googlecode/mjorm/Person.mongo.xml");
		addMapping("/com/googlecode/mjorm/SuperDuperOverride.mongo.xml");
		
		SuperDuper superDuper = new SuperDuper();
		superDuper.setPersonList(new ArrayList<Person>());
		superDuper.setPersonMap(new HashMap<String, Person>());
		superDuper.setPersonSet(new HashSet<Person>());
		superDuper.setPersonSortedSet(new TreeSet<Person>());
		superDuper.setStringMap(new HashMap<String, String>());

		DBObject superDuperDbObject = mapper.unmap(superDuper);
		assertNotNull(superDuperDbObject);
		assertTrue(superDuperDbObject.get("personList") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("personMap") instanceof Map);
		assertTrue(superDuperDbObject.get("personSet") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("personSortedSet") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("stringMap") instanceof Map);
	}

	@Test
	public void testConvert_Identifiers()
		throws Exception {
		addMapping("/com/googlecode/mjorm/City.mongo.xml");

		City city = new City();
		city.setName("city name");
		city.setLat(new Float("123.456"));
		city.setLon(new Float("789.101"));

		DBObject cityDbObject = mapper.unmap(city);
		assertNotNull(cityDbObject);
		assertNotNull(cityDbObject.get("_id"));
		assertEquals(city.getName(), cityDbObject.get("name"));
		assertEquals(city.getLat(), cityDbObject.get("lat"));
		assertEquals(city.getLon(), cityDbObject.get("lon"));
		
		city.setId(new ObjectId().toStringMongod());
		cityDbObject = mapper.unmap(city);
		assertNotNull(cityDbObject);
		assertNotNull(cityDbObject.get("_id"));
		assertTrue(ObjectId.class.isInstance(cityDbObject.get("_id")));
		assertEquals(city.getId(), cityDbObject.get("_id").toString());
		assertEquals(city.getName(), cityDbObject.get("name"));
		assertEquals(city.getLat(), cityDbObject.get("lat"));
		assertEquals(city.getLon(), cityDbObject.get("lon"));

		City transformedCity = mapper.map(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertNotNull(transformedCity.getId());
		assertEquals(city.getId(), transformedCity.getId());
		assertEquals(city.getName(), transformedCity.getName());
		assertEquals(city.getLat(), transformedCity.getLat());
		assertEquals(city.getLon(), transformedCity.getLon());

		cityDbObject.removeField("_id");
		transformedCity = mapper.map(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertNull(transformedCity.getId());
		assertEquals(city.getName(), transformedCity.getName());
		assertEquals(city.getLat(), transformedCity.getLat());
		assertEquals(city.getLon(), transformedCity.getLon());

	}

	@Test
	public void testConvert_WithNulls()
		throws Exception {
		addMapping("/com/googlecode/mjorm/City.mongo.xml");

		City city = new City();
		city.setName(null);
		city.setLat(new Float("123.456"));
		city.setLon(new Float("789.101"));

		DBObject cityDbObject = mapper.unmap(city);
		assertNotNull(cityDbObject);
		assertNull(cityDbObject.get("name"));
		assertEquals(city.getLat(), cityDbObject.get("lat"));
		assertEquals(city.getLon(), cityDbObject.get("lon"));

		City transformedCity = mapper.map(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertNull(transformedCity.getName());
		assertEquals(city.getLat(), transformedCity.getLat());
		assertEquals(city.getLon(), transformedCity.getLon());

	}

	@Test
	public void testConvertToAndFromDBObject_Maps_And_Collections()
		throws Exception {
		addMapping("/com/googlecode/mjorm/Address.mongo.xml");
		addMapping("/com/googlecode/mjorm/Person.mongo.xml");
		addMapping("/com/googlecode/mjorm/SuperDuper.mongo.xml");

		Person p1 = new Person(); p1.setFirstName("p1");
		Person p2 = new Person(); p2.setFirstName("p2");
		Person p3 = new Person(); p3.setFirstName("p3");
		Person p4 = new Person(); p4.setFirstName("p4");
		Person p5 = new Person(); p5.setFirstName("p5");
		
		SuperDuper superDuper = new SuperDuper();
		superDuper.setPersonList(new ArrayList<Person>());
		superDuper.setPersonMap(new HashMap<String, Person>());
		superDuper.setPersonSet(new HashSet<Person>());
		superDuper.setPersonSortedSet(new TreeSet<Person>());
		superDuper.setStringMap(new HashMap<String, String>());
		
		superDuper.getStringMap().put("key0", "val0");
		superDuper.getStringMap().put("key1", "val1");
		superDuper.getStringMap().put("key2", "val2");

		superDuper.getPersonMap().put("key1", p1);
		superDuper.getPersonMap().put("key2", p2);

		superDuper.getPersonList().add(p2);
		superDuper.getPersonList().add(p1);
		superDuper.getPersonList().add(p1);
		superDuper.getPersonList().add(p3);

		superDuper.getPersonSet().add(p1);
		superDuper.getPersonSet().add(p2);
		superDuper.getPersonSet().add(p3);

		superDuper.getPersonSortedSet().add(p3);
		superDuper.getPersonSortedSet().add(p2);
		superDuper.getPersonSortedSet().add(p1);

		DBObject superDuperDbObject = mapper.unmap(superDuper);
		assertNotNull(superDuperDbObject);
		assertTrue(superDuperDbObject.get("personList") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("personMap") instanceof Map);
		assertTrue(superDuperDbObject.get("personSet") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("personSortedSet") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("stringMap") instanceof Map);
		
		SuperDuper convertedSuperDuper = mapper.map(
			superDuperDbObject, SuperDuper.class);
		assertNotNull(convertedSuperDuper);
		assertEquals(superDuper.getPersonList(), convertedSuperDuper.getPersonList());
		assertEquals(superDuper.getPersonSet(), convertedSuperDuper.getPersonSet());
		assertEquals(superDuper.getPersonSortedSet(), convertedSuperDuper.getPersonSortedSet());
		assertEquals(superDuper.getPersonMap(), convertedSuperDuper.getPersonMap());
		assertEquals(superDuper.getStringMap(), convertedSuperDuper.getStringMap());
	}

	@Test
	public void testConvert_ConvertCity_And_AltColumns()
		throws Exception {
		addMapping("/com/googlecode/mjorm/City_alt_columns.mongo.xml");

		City city = new City();
		city.setName("city name");
		city.setLat(new Float("123.456"));
		city.setLon(new Float("789.101"));

		DBObject cityDbObject = mapper.unmap(city);
		assertNotNull(cityDbObject);
		assertNull(cityDbObject.get("_id"));
		assertEquals(city.getName(), cityDbObject.get("col1"));
		assertEquals(city.getLat(), cityDbObject.get("col2"));
		assertEquals(city.getLon(), cityDbObject.get("col3"));

		City transformedCity = mapper.map(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertEquals(city.getName(), transformedCity.getName());
		assertEquals(city.getLat(), transformedCity.getLat());
		assertEquals(city.getLon(), transformedCity.getLon());
		
	}

	@Test
	public void testConvert_Arrays()
		throws Exception {
		addMapping("/com/googlecode/mjorm/Arrays.mongo.xml");

		City city = new City();
		city.setName("city name");
		city.setLat(new Float("123.456"));
		city.setLon(new Float("789.101"));
		city.setZipCodes(new String[] {"zip1", "zip2", "zip3", "zip4"});

		State state = new State();
		state.setName("state name");
		state.setCitiesArray(new City[]{city, null, city});

		DBObject cityDbObject = mapper.unmap(city);
		assertNotNull(cityDbObject);
		assertEquals(city.getName(), cityDbObject.get("name"));
		assertEquals(city.getLat(), cityDbObject.get("lat"));
		assertEquals(city.getLon(), cityDbObject.get("lon"));
		assertNotNull(cityDbObject.get("zipCodes"));
		assertTrue(BasicDBList.class.isInstance(cityDbObject.get("zipCodes")));
		BasicDBList transformedZipCodes = BasicDBList.class.cast(cityDbObject.get("zipCodes"));
		assertArrayEquals(city.getZipCodes(), transformedZipCodes.toArray(new String[0]));

		City transformedCity = mapper.map(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertEquals(city.getName(), transformedCity.getName());
		assertEquals(city.getLat(), transformedCity.getLat());
		assertEquals(city.getLon(), transformedCity.getLon());
		assertArrayEquals(city.getZipCodes(), transformedCity.getZipCodes());

		DBObject stateDbObject = mapper.unmap(state);
		assertNotNull(stateDbObject);
		assertEquals(state.getName(), stateDbObject.get("name"));
		assertNotNull(stateDbObject.get("citiesArray"));
		assertTrue(BasicDBList.class.isInstance(stateDbObject.get("citiesArray")));
		BasicDBList transformedCities = BasicDBList.class.cast(stateDbObject.get("citiesArray"));
		BasicDBObject cityDbObject0 = BasicDBObject.class.cast(transformedCities.get(0));
		BasicDBObject cityDbObject1 = BasicDBObject.class.cast(transformedCities.get(1));
		BasicDBObject cityDbObject2 = BasicDBObject.class.cast(transformedCities.get(2));
		assertNotNull(cityDbObject0);
		assertNull(cityDbObject1);
		assertNotNull(cityDbObject2);
		assertEquals(city.getName(), cityDbObject0.get("name"));
		assertEquals(city.getLat(), cityDbObject0.get("lat"));
		assertEquals(city.getLon(), cityDbObject0.get("lon"));
		assertEquals(city.getName(), cityDbObject2.get("name"));
		assertEquals(city.getLat(), cityDbObject2.get("lat"));
		assertEquals(city.getLon(), cityDbObject2.get("lon"));

		State transformedState = mapper.map(stateDbObject, State.class);
		assertNotNull(transformedState);
		assertEquals(state.getName(), transformedState.getName());
		assertArrayEquals(state.getCitiesArray(), transformedState.getCitiesArray());
		
	}
		

}
