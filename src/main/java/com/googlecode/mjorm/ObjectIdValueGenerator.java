package com.googlecode.mjorm;

import org.bson.types.ObjectId;

public class ObjectIdValueGenerator
	implements ValueGenerator<ObjectId> {

	public static final ObjectIdValueGenerator INSTANCE = new ObjectIdValueGenerator();

	public ObjectId generate() {
		return new ObjectId();
	}

}
