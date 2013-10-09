package com.googlecode.mjorm.query.modifiers;

public class RenameModifier
	extends AbstractValueModifier<String> {

	public RenameModifier(String value) {
		super(value, "$rename");
	}

}
