package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.mql.MqlCriterionFunction;
import com.googlecode.mjorm.mql.AbstractMqlCriterionFunction;
import com.googlecode.mjorm.query.criteria.AbstractCriterion;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.googlecode.mjorm.query.criteria.Criterion;
import com.googlecode.mjorm.query.criteria.EqualsCriterion;
import com.googlecode.mjorm.query.criteria.FieldCriterion;
import com.mongodb.BasicDBList;

public class QueryGroup
	extends AbstractCriterion {

	private List<Query> queryCriterions = new ArrayList<Query>();

	public QueryGroup() {
		
	}

	public QueryGroup(Collection<Query> queries) {
		addAll(queries);
	}

	public QueryGroup(Query... queries) {
		addAll(queries);
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to the list of conditions.
	 * @return the {@link AbstractQueryCriterion}
	 */
	public Query add() {
		Query ret = new Query();
		add(ret);
		return ret;
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to this group.
	 * @param queryCriterion the {@link AbstractQueryCriterion}
	 */
	public QueryGroup add(Query queryCriterion) {
		queryCriterions.add(queryCriterion);
		return this;
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to this group.
	 * @param queryCriterion the {@link AbstractQueryCriterion}
	 */
	public QueryGroup addAll(Collection<Query> queries) {
		queryCriterions.addAll(queries);
		return this;
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to this group.
	 * @param queryCriterion the {@link AbstractQueryCriterion}
	 */
	public QueryGroup addAll(Query... queries) {
		queryCriterions.addAll(Arrays.asList(queries));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		BasicDBList list = new BasicDBList();
		for (Query queryCriterion : queryCriterions) {
			list.add(queryCriterion.toQueryObject(mapper));
		}
		return list;
	}

	public static MqlCriterionFunction createMqlDocumentFunction(
		final String functionName, final String operatorName, final boolean allowQueryGroup, final boolean allowQuery) {
		return createMqlDocumentFunction(
			functionName, operatorName, allowQueryGroup, allowQuery, -1, -1, -1);
	}

	public static MqlCriterionFunction createMqlDocumentFunction(
		final String functionName, final String operatorName,
		final int exactArgs, final int minArgs, final int maxArgs, final Class<?>... types) {
		return createMqlDocumentFunction(
			functionName, operatorName, false, false, exactArgs, minArgs, maxArgs, types);
	}

	public static MqlCriterionFunction createMqlDocumentFunction(
		final String functionName, final String operatorName,
		final int exactArgs, final Class<?>... types) {
		return createMqlDocumentFunction(
			functionName, operatorName, false, false, exactArgs, -1, -1, types);
	}

	public static MqlCriterionFunction createMqlDocumentFunction(
		final String functionName, final String operatorName,
		final boolean allowQueryGroup, final boolean allowQuery,
		final int exactArgs, final int minArgs, final int maxArgs, final Class<?>... types) {
		return new AbstractMqlCriterionFunction() {
			protected void init() {
				setFunctionName(functionName);
				setAllowQueryGroup(allowQueryGroup);
				setAllowQuery(allowQuery);
				setExactArgs(exactArgs);
				setMinArgs(minArgs);
				setMaxArgs(maxArgs);
				setTypes(types);
			}
			
			@Override
			protected Criterion doCreate(Query query) {
				return new FieldCriterion(operatorName, new QueryGroup(query));
			}
			@Override
			protected Criterion doCreate(QueryGroup queryGroup) {
				return new FieldCriterion(operatorName, queryGroup);
			}
			@Override
			protected Criterion doCreate(Object[] values) {
				return new FieldCriterion(operatorName, new EqualsCriterion(values));
			}
		};
	}

}
