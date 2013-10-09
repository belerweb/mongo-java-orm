package com.googlecode.mjorm.query;

import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;

public class Query
	extends AbstractQueryCriterion<Query> {

	/**
	 * Method to make chaining look cleaner.
	 * @return a new {@link Query}
	 */
	public static Query start() {
		return new Query();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Query self() {
		return this;
	}

}
