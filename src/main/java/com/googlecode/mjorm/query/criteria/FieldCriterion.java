package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FieldCriterion
	extends AbstractCriterion
	implements DocumentCriterion {

	private String fieldName;
	private Criterion criterion;

	public FieldCriterion(String fieldName, Criterion criterion) {
		this.fieldName	= fieldName;
		this.criterion	= criterion;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the criterion
	 */
	public Criterion getCriterion() {
		return criterion;
	}

	@Override
	public DBObject toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject(fieldName, criterion.toQueryObject(mapper));
	}

}
