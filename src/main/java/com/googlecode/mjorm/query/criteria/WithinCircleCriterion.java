package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class WithinCircleCriterion
	extends AbstractCriterion {

	private Number[] coords = new Number[2];
	private Number radius;

	public WithinCircleCriterion(Number x, Number y, Number radius) {
		this.coords[0] 	= x;
		this.coords[1] 	= y;
		this.radius 	= radius;
	}

	public Number getX() {
		return this.coords[0];
	}

	public Number getY() {
		return this.coords[1];
	}

	public Number getRadius() {
		return this.radius;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		BasicDBList args = new BasicDBList();
		args.add(coords);
		args.add(radius);
		return new BasicDBObject(
			"$within", new BasicDBObject("$center", args));
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setExactArgs(3);
				setTypes(Number.class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				return new WithinCircleCriterion(
					Number.class.cast(values[0]),
					Number.class.cast(values[1]),
					Number.class.cast(values[2]));
			}
		};
	}
}
