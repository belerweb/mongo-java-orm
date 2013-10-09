package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class ExistsCriterion
	extends AbstractCriterion {

	private Boolean value;

	public ExistsCriterion(Boolean value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject("$exists", value);
	}

	public static MqlCriterionFunction createFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setMinArgs(1);
				setMaxArgs(1);
				setTypes(Boolean.class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				Boolean arg = (values.length>0) ? Boolean.class.cast(values[0]) : true;
				return new ExistsCriterion(arg);
			}
			@Override
			protected Criterion doCreate() {
				return new ExistsCriterion(true);
			}
		};
	}

	public static MqlCriterionFunction createNegatedFunction(final String functionName) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setMinArgs(1);
				setMaxArgs(1);
				setTypes(Boolean.class);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				Boolean arg = (values.length>0) ? Boolean.class.cast(values[0]) : false;
				return new ExistsCriterion(arg);
			}
			@Override
			protected Criterion doCreate() {
				return new ExistsCriterion(false);
			}
		};
	}

}
