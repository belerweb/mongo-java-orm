package com.googlecode.mjorm.query.modifiers;

import java.util.Collection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PushAllModifier
	extends AbstractModifier {

	private Object[] values;

	public PushAllModifier(Object[] values) {
		this.values = new Object[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
	}

	public PushAllModifier(Collection<?> values) {
		this.values = values.toArray(new Object[0]);
	}

	@Override
	public DBObject toModifierObject(String propertyName) {
		return new BasicDBObject("$pushAll",
			new BasicDBObject(propertyName, this.values));
	}

}
