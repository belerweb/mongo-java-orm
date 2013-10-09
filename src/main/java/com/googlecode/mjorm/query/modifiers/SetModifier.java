package com.googlecode.mjorm.query.modifiers;

public class SetModifier
	extends AbstractValueModifier<Object> {

	public SetModifier(Object value) {
		super(value, "$set");
	}

}
