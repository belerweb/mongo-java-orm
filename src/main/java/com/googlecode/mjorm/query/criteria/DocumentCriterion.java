package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.DBObject;

public interface DocumentCriterion
	extends Criterion {

	DBObject toQueryObject(ObjectMapper mapper);
}
