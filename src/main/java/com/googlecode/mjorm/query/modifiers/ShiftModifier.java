package com.googlecode.mjorm.query.modifiers;

public class ShiftModifier
	extends AbstractValueModifier<Number> {

	public ShiftModifier() {
		super(-1, "$pop");
	}

}
