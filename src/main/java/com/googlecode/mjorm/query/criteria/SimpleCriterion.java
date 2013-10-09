package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.mongodb.BasicDBObject;

public class SimpleCriterion
	extends AbstractCriterion {

	public enum Operator {
		GT		("$gt"),
		GTE		("$gte"),
		LT		("$lt"),
		LTE		("$lte"),
		NE		("$ne"),
		IN		("$in"),
		NIN		("$nin"),
		ALL		("$all")
		;
		private String operator;
		Operator(String operator) {
			this.operator = operator;
		}
		public String getOperatorString() {
			return this.operator;
		}
	}

	private String operator;
	private Object value;

	public SimpleCriterion(String operator, Object value) {
		this.operator	= operator;
		this.value		= value;
	}

	public SimpleCriterion(Operator operator, Object value) {
		this(operator.getOperatorString(), value);
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject(operator, mapper.unmapValue(value));
	}

	/**	 
	 * Creates an {@link MqlCriterionFunction} for the given {@link Operator}
	 * with the given restrictions.
	 * @param operator
	 * @param minArgs
	 * @param maxArgs
	 * @param exactArgs
	 * @param types
	 * @return
	 */
	public static final MqlCriterionFunction createForOperator(
		final String functionName, final Operator operator, 
		final int minArgs, final int maxArgs, final int exactArgs,
		final Class<?>... types) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setMinArgs(minArgs);
				setMaxArgs(maxArgs);
				setExactArgs(exactArgs);
				setTypes(types);
			}
			@Override
			public Criterion createForArguments(Object[] values) {
				return new SimpleCriterion(operator, values);
			}
		};
	}

}
