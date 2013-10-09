package com.googlecode.mjorm.query.modifiers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BitwiseModifier
	extends AbstractModifier {

	private Operation operation;
	private Number value;

	public BitwiseModifier(Operation operation, Number value) {
		this.operation 	= operation;
		this.value		= value;
	}

	@Override
	public DBObject toModifierObject(String propertyName) {
		return new BasicDBObject("$bit",
			new BasicDBObject(propertyName,
				new BasicDBObject(operation.getValue(), value)));
	}

	public enum Operation {
		AND("and"),
		OR("or")
		;
		String value;
		Operation(String value) {
			this.value = value;
		}
		public String getValue() {
			return this.value;
		}
		
	}

}
