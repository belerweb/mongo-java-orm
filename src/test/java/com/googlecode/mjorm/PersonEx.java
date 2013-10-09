package com.googlecode.mjorm;

public class PersonEx
	extends Person {

	public String getFullName() {
		return super.getFirstName()+" "+super.getLastName();
	}

}
