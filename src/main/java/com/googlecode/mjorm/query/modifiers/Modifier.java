package com.googlecode.mjorm.query.modifiers;

import com.mongodb.DBObject;

/**
 * Represents a single query modifier.
 * 
 */
public interface Modifier {

	/**
	 * Returns the query representation of the {@code Modifier}.
	 * Most of the time this will be a {@link DBObject}, but
	 * it can be anything accepted by the MongoDB java driver.
	 * 
	 * @return the {@link Object}
	 */
	DBObject toModifierObject(String propertyName);
}
