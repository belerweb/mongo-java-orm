package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Id;
import com.googlecode.mjorm.annotations.Property;

@Entity
public class AnnotatedPerson {
	private String id;
	private String firstName;
	private String lastName;
	private AnnotatedAddress address;

	@Id
	@Property
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Property
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Property
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Property
	public AnnotatedAddress getAddress() {
		return address;
	}

	public void setAddress(AnnotatedAddress address) {
		this.address = address;
	}
	
}