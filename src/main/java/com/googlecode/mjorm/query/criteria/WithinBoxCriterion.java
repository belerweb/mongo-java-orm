package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class WithinBoxCriterion
	extends AbstractCriterion {

	private Number[][] coords = new Number[2][2];

	public WithinBoxCriterion(Number x, Number y, Number xx, Number yy) {
		this.coords[0][0] 		= x;
		this.coords[0][1] 		= y;
		this.coords[1][0] 		= xx;
		this.coords[1][1] 		= yy;
	}

	public Number getX() {
		return this.coords[0][0];
	}

	public Number getY() {
		return this.coords[0][1];
	}

	public Number getXX() {
		return this.coords[1][0];
	}

	public Number getYY() {
		return this.coords[1][1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject(
			"$within", new BasicDBObject("$box", coords));
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setExactArgs(4);
				setTypes(Number.class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				return new WithinBoxCriterion(
					Number.class.cast(values[0]),
					Number.class.cast(values[1]),
					Number.class.cast(values[2]),
					Number.class.cast(values[2]));
			}
		};
	}
}
