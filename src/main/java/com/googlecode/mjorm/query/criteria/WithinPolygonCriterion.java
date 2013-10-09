package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class WithinPolygonCriterion
	extends AbstractCriterion {

	private Number[][] points;

	public WithinPolygonCriterion(Number[][] points) {
		this.points = points;
	}

	public Number[][] getPoints() {
		return this.points;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject(
			"$within", new BasicDBObject("$polygon", points));
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setMinArgs(1);
				setMaxArgs(Integer.MAX_VALUE);
				setTypes(Object[].class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				Number[][] points = new Number[values.length][];
				for (int i=0; i<values.length; i++) {
					Object[] realValues = Object[].class.cast(values[i]);
					points[i] = new Number[2];
					points[i][0] = Number.class.cast(realValues[0]);
					points[i][1] = Number.class.cast(realValues[1]);
				}
				return new WithinPolygonCriterion(points);
			}
		};
	}
}
