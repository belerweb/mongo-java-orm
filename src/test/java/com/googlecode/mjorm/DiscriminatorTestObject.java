package com.googlecode.mjorm;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Id;
import com.googlecode.mjorm.annotations.Property;
import com.googlecode.mjorm.annotations.SubClass;

@Entity(
	discriminatorName = "disc",
	discriminatorType = DiscriminatorType.STRING,
	subClasses={
		@SubClass(discriminiatorValue="subClassOne", entityClass=TestObjectSubClassOne.class),
		@SubClass(discriminiatorValue="subClassTwo", entityClass=TestObjectSubClassTwo.class)
	}
)
public class DiscriminatorTestObject {

	private String id;
	private String name;
	private String disc;

	/**
	 * @return the id
	 */
	@Id
	@Property
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@Property
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the disc
	 */
	@Property
	public String getDisc() {
		return disc;
	}

	/**
	 * @param disc the disc to set
	 */
	public void setDisc(String disc) {
		this.disc = disc;
	}

}
