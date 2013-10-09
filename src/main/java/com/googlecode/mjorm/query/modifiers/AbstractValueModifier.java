package com.googlecode.mjorm.query.modifiers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class AbstractValueModifier<T>
	extends AbstractModifier {

	private T value;
	private String command;

	public AbstractValueModifier(T value, String command) {
		this.value 		= value;
		this.command	= command;
	}

	protected T getValue() {
		return value;
	}

	protected String getCommand() {
		return command;
	}

	@Override
	public DBObject toModifierObject(String propertyName) {
		return new BasicDBObject(command,
			new BasicDBObject(propertyName, value));
	}

}
