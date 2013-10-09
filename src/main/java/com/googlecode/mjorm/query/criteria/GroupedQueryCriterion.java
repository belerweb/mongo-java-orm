package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.query.QueryGroup;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class GroupedQueryCriterion
	extends AbstractCriterion
	implements DocumentCriterion {

	public enum Group {
		OR("$or"),
		NOR("$nor"),
		AND("$and")
		;
		private String name;
		Group(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	}

	private Group group;
	private QueryGroup queryGroup;

	public GroupedQueryCriterion(Group group, QueryGroup queryGroup) {
		this.group = group;
		this.queryGroup = queryGroup;
	}

	@Override
	public DBObject toQueryObject(ObjectMapper mapper) {
		return new BasicDBObject(
			group.getName(), queryGroup.toQueryObject(mapper));
	}

}
