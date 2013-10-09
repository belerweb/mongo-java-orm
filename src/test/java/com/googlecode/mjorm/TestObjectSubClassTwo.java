package com.googlecode.mjorm;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Property;

@Entity
public class TestObjectSubClassTwo
	extends DiscriminatorTestObject {

	private String two;

	/**
	 * @return the two
	 */
	@Property
	public String getTwo() {
		return two;
	}

	/**
	 * @param two the two to set
	 */
	public void setTwo(String two) {
		this.two = two;
	}

}
