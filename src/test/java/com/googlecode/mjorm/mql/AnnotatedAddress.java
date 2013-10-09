package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Property;

@Entity
public class AnnotatedAddress {
	private String street;
	private String city;
	private String state;
	private String zipCode;

	@Property
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Property
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Property
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Property
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}