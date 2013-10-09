package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;

/**
 * Represents a single query criteria.
 * 
 */
public interface Criterion {

	/**
	 * Returns the query representation of the {@code Criterion}.
	 * Most of the time this will be a {@link DBObject}, but
	 * it can be anything accepted by the MongoDB java driver.
	 * 
	 * @param mapper the {@link ObjectMapper}
	 * @return the {@link Object}
	 */
	Object toQueryObject(ObjectMapper mapper);

}
