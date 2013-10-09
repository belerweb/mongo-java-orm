package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class SizeCriterion
	extends AbstractCriterion {

	private Number size;

	public SizeCriterion(Number size) {
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public Number getSize() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject("$size", size);
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setExactArgs(1);
				setTypes(Number.class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				return new SizeCriterion(Number.class.cast(values[0]));
			}
		};
	}
}
