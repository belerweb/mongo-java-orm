package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class NotCriterion
	extends FieldCriterion {

	public NotCriterion(String fieldName, Criterion criterion) {
		super(fieldName, criterion);
	}

	public NotCriterion(FieldCriterion criterion) {
		this(criterion.getFieldName(), criterion.getCriterion());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject(getFieldName(),
				new BasicDBObject("$not", getCriterion().toQueryObject(mapper)));
	}

}
