package com.googlecode.mjorm.query.modifiers;

public class UnSetModifier
	extends AbstractValueModifier<Object> {

	public UnSetModifier() {
		super(1, "$unset");
	}

}
