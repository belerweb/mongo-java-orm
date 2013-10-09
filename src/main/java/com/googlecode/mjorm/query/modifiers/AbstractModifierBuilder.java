package com.googlecode.mjorm.query.modifiers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import com.googlecode.mjorm.DBObjectUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class AbstractModifierBuilder<T extends AbstractModifierBuilder<T>> {

	protected Stack<String> propertyStack = new Stack<String>();
	protected Map<String, List<Modifier>> modifiersMap = new LinkedHashMap<String, List<Modifier>>();

	protected abstract T self();

	/**
	 * Clears the query.
	 */
	public void clear() {
		modifiersMap.clear();
		propertyStack.clear();
	}

	/**
	 * Returns the {@link Modifier} specified for a
	 * given field.
	 * @param property the field
	 * @return the {@link Modifier}
	 */
	public Modifier[] getModifiers(String property) {
		String key = propertyHierarchy(property);
		if (!modifiersMap.containsKey(key)) {
			return new Modifier[0];
		}
		return modifiersMap.get(key).toArray(new Modifier[0]);
	}

	/**
	 * Indicates whether or not there are {@link Modifiers}
	 * specified for the given property.
	 * @param property the property
	 * @return true or false
	 */
	public Boolean hasModifiersFor(String property) {
		return getModifiers(property).length > 0;
	}

	/**
	 * Pushed a property onto the property stack.
	 * @return the {@link AbstractQueryModifier} for chaining
	 */
	public T push(String property) {
		propertyStack.push(property);
		return self();
	}

	/**
	 * Pops a property off of the property stack.
	 * @return the {@link AbstractQueryModifier} for chaining
	 */
	public T pop() {
		propertyStack.pop();
		return self();
	}

	/**
	 * Adds a {@link Modifier} to the query.
	 * @param property the property name
	 * @param modifier the {@link Modifier}
	 * @return the {@link AbstractQueryModifier} for chaining
	 */
	public T add(String property, Modifier modifier) {
		String key = propertyHierarchy(property);
		if (!modifiersMap.containsKey(key)) {
			modifiersMap.put(key, new ArrayList<Modifier>());
		}
		modifiersMap.get(key).add(modifier);
		return self();
	}

	/**
	 * Returns an array of the current property
	 * hierarchy plus the property given.
	 * @param property the property
	 * @return an array
	 */
	protected String propertyHierarchy(String property) {
		String ret = "";
		for (int i=0; i<propertyStack.size(); i++) {
			ret += propertyStack.get(i) + ".";
		}
		ret += property;
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		Object queryObj = toModifierObject();
		return (queryObj!=null) ? queryObj.toString() : "null";
	}

	/**
	 * {@inheritDoc}
	 */
	public DBObject toModifierObject() {

		// the return object
		BasicDBObject ret = new BasicDBObject();

		for (Entry<String, List<Modifier>> entry : modifiersMap.entrySet()) {

			// get property name
			String propertyName = entry.getKey();

			// loop through the modifiers
			for (Modifier modifier : entry.getValue()) {

				// convert to modifier object
				DBObject curVal = modifier.toModifierObject(propertyName);

				// merge it
				DBObjectUtil.merge(curVal, ret);
			}
		}

		// return it
		return ret;
	}

}