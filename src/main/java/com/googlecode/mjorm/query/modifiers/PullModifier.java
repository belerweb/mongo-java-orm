package com.googlecode.mjorm.query.modifiers;

public class PullModifier
	extends AbstractValueModifier<Object> {

	public PullModifier(Object value) {
		super(value, "$pull");
	}

}
