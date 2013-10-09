package com.googlecode.mjorm.query.modifiers;

public class IncrementModifier
	extends AbstractValueModifier<Number> {

	public IncrementModifier(Number value) {
		super(value, "$inc");
	}

}
