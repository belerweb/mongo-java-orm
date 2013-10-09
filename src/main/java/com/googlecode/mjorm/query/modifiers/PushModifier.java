package com.googlecode.mjorm.query.modifiers;

public class PushModifier
	extends AbstractValueModifier<Object> {

	public PushModifier(Object value) {
		super(value, "$push");
	}

}
