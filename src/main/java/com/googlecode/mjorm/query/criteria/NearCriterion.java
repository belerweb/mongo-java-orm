package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class NearCriterion
	extends AbstractCriterion {

	private Number[] coords = new Number[2];
	private Number distance;

	public NearCriterion(Number x, Number y, Number distance) {
		this.coords[0] 		= x;
		this.coords[1] 		= y;
		this.distance 		= distance;
	}

	public NearCriterion(Number x, Number y) {
		this(x, y, null);
	}

	/**
	 * @return the x
	 */
	public Number getX() {
		return coords[0];
	}

	/**
	 * @return the y
	 */
	public Number getY() {
		return coords[1];
	}

	/**
	 * @return the distance
	 */
	public Number getDistance() {
		return distance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		BasicDBObject ret = new BasicDBObject();
		ret.put("$near", coords);
		if (distance!=null) {
			ret.put("$maxDistance", distance);
		}
		return ret;
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setMinArgs(2);
				setMaxArgs(3);
				setTypes(Number.class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				return (values.length==3)
					? new NearCriterion(
						Number.class.cast(values[0]),
						Number.class.cast(values[1]),
						Number.class.cast(values[2]))
					: new NearCriterion(
						Number.class.cast(values[0]),
						Number.class.cast(values[1]));
			}
		};
	}
}
