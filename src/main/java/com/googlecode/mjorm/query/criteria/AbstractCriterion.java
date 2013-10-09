package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;

public abstract class AbstractCriterion
	implements Criterion {

	/**
	 * {@inheritDoc}
	 */
	public abstract Object toQueryObject(ObjectMapper mapper);

}
