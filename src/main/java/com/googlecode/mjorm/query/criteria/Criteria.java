package com.googlecode.mjorm.query.criteria;

import java.util.Collection;
import java.util.regex.Pattern;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.SimpleCriterion.Operator;
import com.googlecode.mjorm.query.criteria.TypeCriterion.Type;

/**
 * Utility class for easily creating {@link Criterion}.
 */
public class Criteria {
	
	/**
	 * {@see EqualsCriterion}
	 */
	public static <V> EqualsCriterion eq(V value) {
		return new EqualsCriterion(value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion gt(V value) {
		return new SimpleCriterion(Operator.GT, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion gte(V value) {
		return new SimpleCriterion(Operator.GTE, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion lt(V value) {
		return new SimpleCriterion(Operator.LT, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion lte(V value) {
		return new SimpleCriterion(Operator.LTE, value);
	}
	
	/**
	 * {@see BetweenCriterion}
	 */
	public static <V> BetweenCriterion between(V left, V right) {
		return new BetweenCriterion(left, right);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion ne(V value) {
		return new SimpleCriterion(Operator.NE, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion in(V... values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion in(Collection<V> values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion nin(V... values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion nin(Collection<V> values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion all(V... values) {
		return new SimpleCriterion(Operator.ALL, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion all(Collection<V> values) {
		return new SimpleCriterion(Operator.ALL, values);
	}
	
	/**
	 * {@see ExistsCriterion}
	 */
	public static ExistsCriterion exists(Boolean value) {
		return new ExistsCriterion(value);
	}
	
	/**
	 * {@see ModCriterion}
	 */
	public static ModCriterion mod(Number left, Number right) {
		return new ModCriterion(left, right);
	}
	
	/**
	 * {@see RegexCriterion}
	 */
	public static RegexCriterion regex(Pattern pattern) {
		return new RegexCriterion(pattern);
	}
	
	/**
	 * {@see RegexCriterion}
	 */
	public static RegexCriterion regex(String pattern) {
		return new RegexCriterion(pattern);
	}
	
	/**
	 * {@see RegexCriterion}
	 */
	public static RegexCriterion regex(String pattern, int flags) {
		return new RegexCriterion(pattern, flags);
	}
	
	/**
	 * {@see SizeCriterion}
	 */
	public static SizeCriterion size(Number size) {
		return new SizeCriterion(size);
	}
	
	/**
	 * {@see NearCriterion}
	 */
	public static NearCriterion near(Number x, Number y, Number distance) {
		return new NearCriterion(x, y, distance);
	}
	
	/**
	 * {@see NearCriterion}
	 */
	public static NearCriterion near(Number x, Number y) {
		return new NearCriterion(x, y);
	}
	
	/**
	 * {@see WithinBoxCriterion}
	 */
	public static WithinBoxCriterion within(Number x, Number y, Number xx, Number yy) {
		return new WithinBoxCriterion(x, y, xx, yy);
	}
	
	/**
	 * {@see WithinCircleCriterion}
	 */
	public static WithinCircleCriterion within(Number x, Number y, Number radius) {
		return new WithinCircleCriterion(x, y, radius);
	}
	
	/**
	 * {@see WithinCircleCriterion}
	 */
	public static WithinPolygonCriterion within(Number[][] points) {
		return new WithinPolygonCriterion(points);
	}
	
	/**
	 * {@see TypeCriterion}
	 */
	public static TypeCriterion type(Number typeCode) {
		return new TypeCriterion(typeCode);
	}
	
	/**
	 * {@see TypeCriterion}
	 */
	public static TypeCriterion type(Type type) {
		return new TypeCriterion(type);
	}
	
	/**
	 * {@see ElemMatchCriterion}
	 */
	public static ElemMatchCriterion elemMatch() {
		return new ElemMatchCriterion();
	}
	
	/**
	 * {@see ElemMatchCriterion}
	 */
	public static ElemMatchCriterion elemMatch(Query queryCriterion) {
		return new ElemMatchCriterion(queryCriterion);
	}
	
	/**
	 * {@see QueryGroup}
	 */
	public static QueryGroup group() {
		return new QueryGroup();
	}
	
	/**
	 * {@see QueryGroup}
	 */
	public static QueryGroup group(Query... queries) {
		QueryGroup ret = new QueryGroup();
		for (Query queryCriterion : queries) {
			ret.add(queryCriterion);
		}
		return ret;
	}
	
	/**
	 * {@see NotCriterion}
	 */
	public static NotCriterion not(FieldCriterion criteria) {
		return new NotCriterion(criteria);
	}
	
	/**
	 * {@see NotCriterion}
	 */
	public static NotCriterion not(String fieldName, Criterion criteria) {
		return new NotCriterion(fieldName, criteria);
	}

}
