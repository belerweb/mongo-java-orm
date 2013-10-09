package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.Criterion;

public abstract class AbstractMqlCriterionFunction
	extends AbstractMqlFunction
	implements MqlCriterionFunction {

	private boolean allowQueryGroup		= false;
	private boolean allowQuery			= false;

	protected AbstractMqlCriterionFunction() {
		super();
	}

	protected Criterion doCreate(Object[] values) {
		throw new IllegalArgumentException(
			getFunctionName()+" doesn't implement doCreate(Object[])");
	}

	protected Criterion doCreate() {
		throw new IllegalArgumentException(
			getFunctionName()+" doesn't implement doCreate()");
	}

	protected Criterion doCreate(Query query) {
		throw new IllegalArgumentException(
			getFunctionName()+" doesn't implement doCreate(Query)");
	}

	protected Criterion doCreate(QueryGroup queryGroup) {
		throw new IllegalArgumentException(
			getFunctionName()+" doesn't implement doCreate(QueryGroup)");
	}
	
	public Criterion createForQuery(Query query) {
		if (!allowQuery) {
			throw new IllegalArgumentException(
				getFunctionName()+" doesn't take Query as an argument");
		}
		return doCreate(query);
	}

	public Criterion createForQueryGroup(QueryGroup queryGroup) {
		if (!allowQueryGroup) {
			throw new IllegalArgumentException(
				getFunctionName()+" doesn't take QueryGroup as an argument");
		}
		return doCreate(queryGroup);
	}

	public Criterion createForArguments(Object[] values) {
		assertCorrectNumberOfArguments(values);
		return doCreate(values);
	}

	public Criterion createForNoArguments() {
		return doCreate();
	}

	/**
	 * @return the allowQueryGroup
	 */
	protected boolean isAllowQueryGroup() {
		return allowQueryGroup;
	}

	/**
	 * @param allowQueryGroup the allowQueryGroup to set
	 */
	protected void setAllowQueryGroup(boolean allowQueryGroup) {
		assertNotInitialized();
		this.allowQueryGroup = allowQueryGroup;
	}

	/**
	 * @return the allowQuery
	 */
	protected boolean isAllowQuery() {
		return allowQuery;
	}

	/**
	 * @param allowQuery the allowQuery to set
	 */
	protected void setAllowQuery(boolean allowQuery) {
		assertNotInitialized();
		this.allowQuery = allowQuery;
	}

}