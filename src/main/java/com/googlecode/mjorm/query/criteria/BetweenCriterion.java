package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class BetweenCriterion
	extends AbstractCriterion {

	private Object left;
	private Object right;

	public BetweenCriterion(Object left, Object right) {
		this.left	= left;
		this.right	= right;
	}

	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		BasicDBObject ret = new BasicDBObject();
		ret.put("$gte", mapper.unmapValue(left));
		ret.put("$lte", mapper.unmapValue(right));
		return ret;
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setMaxArgs(1);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				return new BetweenCriterion(values[0], values[1]);
			}
		};
	}

}
