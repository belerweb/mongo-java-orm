package com.googlecode.mjorm.query.modifiers;

import java.util.Collection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PullAllModifier
	extends AbstractModifier {

	private Object[] values;

	public PullAllModifier(Object[] values) {
		this.values = new Object[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
	}

	public PullAllModifier(Collection<?> values) {
		this.values = values.toArray(new Object[0]);
	}

	@Override
	public DBObject toModifierObject(String propertyName) {
		return new BasicDBObject("$pullAll",
			new BasicDBObject(propertyName, this.values));
	}

}
