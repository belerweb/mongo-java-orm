package com.googlecode.mjorm;

import java.util.Set;

public class State {

	private String name;
	private Set<City> cities;
	private City[] citiesArray;
	/**
	 * @return the name
	 */
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
	 * @return the cities
	 */
	public Set<City> getCities() {
		return cities;
	}
	/**
	 * @param cities the cities to set
	 */
	public void setCities(Set<City> cities) {
		this.cities = cities;
	}
	/**
	 * @return the citiesArray
	 */
	public City[] getCitiesArray() {
		return citiesArray;
	}
	/**
	 * @param citiesArray the citiesArray to set
	 */
	public void setCitiesArray(City[] citiesArray) {
		this.citiesArray = citiesArray;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cities == null) ? 0 : cities.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		State other = (State) obj;
		if (cities == null) {
			if (other.cities != null) return false;
		} else if (!cities.equals(other.cities)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
	}

}
