package com.googlecode.mjorm.query.criteria;

import java.util.Collection;
import java.util.regex.Pattern;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.GroupedQueryCriterion.Group;
import com.googlecode.mjorm.query.criteria.TypeCriterion.Type;

/**
 * An object for building MongoDB queries using
 * {@link Criterion}.
 * 
 * @see Criterion
 * @see Criteria
 */
public abstract class AbstractQueryCriterion<T extends AbstractQueryCriterion<T>>
	extends AbstractCriterionBuilder<T> {

	/**
	 * Adds a {@link QueryGroup} for {@code $or}.
	 * @param group
	 * @return
	 */
	public T or(QueryGroup group) {
		add(new GroupedQueryCriterion(Group.OR, group));
		return self();
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $or}.
	 * @param queries
	 * @return
	 */
	public T or(Query... queries) {
		return or(new QueryGroup(queries));
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $or}.
	 * @param queries
	 * @return
	 */
	public T or(Collection<Query> queries) {
		return or(new QueryGroup(queries));
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $nor}.
	 * @param group
	 * @return
	 */
	public T nor(QueryGroup group) {
		add(new GroupedQueryCriterion(Group.NOR, group));
		return self();
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $nor}.
	 * @param queries
	 * @return
	 */
	public T nor(Query... queries) {
		return or(new QueryGroup(queries));
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $nor}.
	 * @param queries
	 * @return
	 */
	public T nor(Collection<Query> queries) {
		return or(new QueryGroup(queries));
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $and}.
	 * @param group
	 * @return
	 */
	public T and(QueryGroup group) {
		add(new GroupedQueryCriterion(Group.AND, group));
		return self();
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $and}.
	 * @param queries
	 * @return
	 */
	public T and(Query... queries) {
		return or(new QueryGroup(queries));
	}

	/**
	 * Adds a {@link QueryGroup} for {@code $and}.
	 * @param queries
	 * @return
	 */
	public T and(Collection<Query> queries) {
		return or(new QueryGroup(queries));
	}

	/**
	 * {@see Criteria#eq(Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T eq(String property, V value) {
		return add(property, Criteria.eq(value));
	}

	/**
	 * {@see Criteria#gt(Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T gt(String property, V value) {
		return add(property, Criteria.gt(value));
	}

	/**
	 * {@see Criteria#gte(Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T gte(String property, V value) {
		return add(property, Criteria.gte(value));
	}

	/**
	 * {@see Criteria#lt(Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T lt(String property, V value) {
		return add(property, Criteria.lt(value));
	}

	/**
	 * {@see Criteria#lte(Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T lte(String property, V value) {
		return add(property, Criteria.lte(value));
	}

	/**
	 * {@see Criteria#between(Object, Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T between(String property, V left, V right) {
		return add(property, Criteria.between(left, right));
	}

	/**
	 * {@see Criteria#ne(Object)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T ne(String property, V value) {
		return add(property, Criteria.ne(value));
	}

	/**
	 * {@see Criteria#in(T[])}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T in(String property, V... values) {
		return add(property, Criteria.in(values));
	}

	/**
	 * {@see Criteria#in(Collection)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T in(String property, Collection<V> values) {
		return add(property, Criteria.in(values));
	}

	/**
	 * {@see Criteria#nin(T[])}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T nin(String property, V... values) {
		return add(property, Criteria.nin(values));
	}

	/**
	 * {@see Criteria#nin(Collection)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T nin(String property, Collection<V> values) {
		return add(property, Criteria.nin(values));
	}

	/**
	 * {@see Criteria#all(T[])}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public<V> T all(String property, V... values) {
		return add(property, Criteria.all(values));
	}

	/**
	 * {@see Criteria#all(Collection)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public <V> T all(String property, Collection<V> values) {
		return add(property, Criteria.all(values));
	}

	/**
	 * {@see Criteria#exists(Boolean)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T exists(String property, Boolean value) {
		return add(property, Criteria.exists(value));
	}

	/**
	 * {@see Criteria#mod(Number, Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T mod(String property, Number left, Number right) {
		return add(property, Criteria.mod(left, right));
	}

	/**
	 * {@see Criteria#regex(Pattern)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T regex(String property, Pattern pattern) {
		return add(property, Criteria.regex(pattern));
	}

	/**
	 * {@see Criteria#regex(String)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T regex(String property, String pattern) {
		return add(property, Criteria.regex(pattern));
	}

	/**
	 * {@see Criteria#regex(String, int)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T regex(String property, String pattern, int flags) {
		return add(property, Criteria.regex(pattern, flags));
	}

	/**
	 * {@see Criteria#size(Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T size(String property, Number size) {
		return add(property, Criteria.size(size));
	}

	/**
	 * {@see Criteria#type(Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T type(String property, Number typeCode) {
		return add(property, Criteria.type(typeCode));
	}

	/**
	 * {@see Criteria#type(Type)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T type(String property, Type type) {
		return add(property, Criteria.type(type));
	}

	/**
	 * {@see Criteria#elemMatch(AbstractQueryCriterion)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T elemMatch(String property, Query queryCriterion) {
		return add(property, Criteria.elemMatch(queryCriterion));
	}

	/**
	 * {@see Criteria#not(Criterion)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T not(String property, Criterion criteria) {
		return add(Criteria.not(property, criteria));
	}

	/**
	 * {@see Criteria#not(Criterion)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T not(FieldCriterion criterion) {
		return add(Criteria.not(criterion));
	}

	/**
	 * {@see Criteria#near(Number, Number, Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T near(String property, Number x, Number y, Number distance) {
		return add(property, Criteria.near(x, y, distance));
	}

	/**
	 * {@see Criteria#near(Number, Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T near(String property, Number x, Number y) {
		return add(property, Criteria.near(x, y));
	}

	/**
	 * {@see Criteria#within(Number, Number, Number, Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T within(String property, Number x, Number y, Number xx, Number yy) {
		return add(property, Criteria.within(x, y, xx, yy));
	}

	/**
	 * {@see Criteria#within(Number, Number, Number)}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T within(String property, Number x, Number y, Number radius) {
		return add(property, Criteria.within(x, y, radius));
	}

	/**
	 * {@see Criteria#within(Number[][])}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T within(String property, Number[][] points) {
		return add(property, Criteria.within(points));
	}
}
