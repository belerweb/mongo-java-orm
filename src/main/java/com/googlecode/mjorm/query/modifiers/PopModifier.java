package com.googlecode.mjorm.query.modifiers;

public class PopModifier
	extends AbstractValueModifier<Number> {

	public PopModifier() {
		super(1, "$pop");
	}

}
