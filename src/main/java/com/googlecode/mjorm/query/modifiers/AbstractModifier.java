package com.googlecode.mjorm.query.modifiers;

import com.mongodb.DBObject;

public abstract class AbstractModifier
	implements Modifier {

	/**
	 * {@inheritDoc}
	 */
	public abstract DBObject toModifierObject(String propertyName);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		Object queryObj = toModifierObject("[propertyName]");
		return (queryObj!=null) ? queryObj.toString() : "null";
	}

}
