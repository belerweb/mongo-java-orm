package com.googlecode.mjorm;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class SuperDuper {

	private Set<Person> personSet;
	private List<Person> personList;
	private SortedSet<Person> personSortedSet;
	private Map<String, Person> personMap;
	private Map<String, String> stringMap;
	/**
	 * @return the personSet
	 */
	public Set<Person> getPersonSet() {
		return personSet;
	}
	/**
	 * @param personSet the personSet to set
	 */
	public void setPersonSet(Set<Person> personSet) {
		this.personSet = personSet;
	}
	/**
	 * @return the personList
	 */
	public List<Person> getPersonList() {
		return personList;
	}
	/**
	 * @param personList the personList to set
	 */
	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}
	/**
	 * @return the personSortedSet
	 */
	public SortedSet<Person> getPersonSortedSet() {
		return personSortedSet;
	}
	/**
	 * @param personSortedSet the personSortedSet to set
	 */
	public void setPersonSortedSet(SortedSet<Person> personSortedSet) {
		this.personSortedSet = personSortedSet;
	}
	/**
	 * @return the personMap
	 */
	public Map<String, Person> getPersonMap() {
		return personMap;
	}
	/**
	 * @param personMap the personMap to set
	 */
	public void setPersonMap(Map<String, Person> personMap) {
		this.personMap = personMap;
	}
	/**
	 * @return the stringMap
	 */
	public Map<String, String> getStringMap() {
		return stringMap;
	}
	/**
	 * @param stringMap the stringMap to set
	 */
	public void setStringMap(Map<String, String> stringMap) {
		this.stringMap = stringMap;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((personList == null) ? 0 : personList.hashCode());
		result = prime * result + ((personMap == null) ? 0 : personMap.hashCode());
		result = prime * result + ((personSet == null) ? 0 : personSet.hashCode());
		result = prime * result + ((personSortedSet == null) ? 0 : personSortedSet.hashCode());
		result = prime * result + ((stringMap == null) ? 0 : stringMap.hashCode());
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
		SuperDuper other = (SuperDuper) obj;
		if (personList == null) {
			if (other.personList != null) return false;
		} else if (!personList.equals(other.personList)) return false;
		if (personMap == null) {
			if (other.personMap != null) return false;
		} else if (!personMap.equals(other.personMap)) return false;
		if (personSet == null) {
			if (other.personSet != null) return false;
		} else if (!personSet.equals(other.personSet)) return false;
		if (personSortedSet == null) {
			if (other.personSortedSet != null) return false;
		} else if (!personSortedSet.equals(other.personSortedSet)) return false;
		if (stringMap == null) {
			if (other.stringMap != null) return false;
		} else if (!stringMap.equals(other.stringMap)) return false;
		return true;
	}

}
