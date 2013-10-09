package com.googlecode.mjorm.query.modifiers;

public class AddToSetModifier
	extends AbstractValueModifier<Object> {

	public AddToSetModifier(Object value) {
		super(value, "$addToSet");
	}

}
