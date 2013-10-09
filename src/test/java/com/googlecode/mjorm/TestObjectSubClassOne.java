package com.googlecode.mjorm;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Property;

@Entity
public class TestObjectSubClassOne
	extends DiscriminatorTestObject {

	private String one;

	/**
	 * @return the one
	 */
	@Property
	public String getOne() {
		return one;
	}

	/**
	 * @param one the one to set
	 */
	public void setOne(String one) {
		this.one = one;
	}

}
