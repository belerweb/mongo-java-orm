package com.googlecode.mjorm.query.modifiers;

import java.util.Collection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class AddToSetEachModifier
	extends AbstractModifier {

	private Object[] values;

	public AddToSetEachModifier(Object[] values) {
		this.values = new Object[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
	}

	public AddToSetEachModifier(Collection<?> values) {
		this.values = values.toArray(new Object[0]);
	}

	@Override
	public DBObject toModifierObject(String propertyName) {
		return new BasicDBObject("$addToSet",
			new BasicDBObject(propertyName,
				new BasicDBObject("$each", this.values)));
	}

}
