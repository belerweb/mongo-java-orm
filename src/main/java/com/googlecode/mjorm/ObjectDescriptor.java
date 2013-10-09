package com.googlecode.mjorm;

import java.util.HashMap;
import java.util.Map;

/**
 * An object that describes how to convert
 * java objects to and from {@link DBObject}s.
 */
public class ObjectDescriptor {

	private Class<?> type;
	private Map<String, PropertyDescriptor> properties
		= new HashMap<String, PropertyDescriptor>();

	private String discriminatorName;
	private String discriminatorType;
	private Map<Object, ObjectDescriptor> subClassObjectDescriptors
		= new HashMap<Object, ObjectDescriptor>();

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> objectClass) {
		this.type	= objectClass;
	}

	/**
	 * @return the discriminator
	 */
	public String getDiscriminatorName() {
		return discriminatorName;
	}

	/**
	 * @param discriminator the discriminator to set
	 */
	public void setDiscriminatorName(String discriminatorName) {
		this.discriminatorName = discriminatorName;
	}

	/**
	 * @return the discriminatorType
	 */
	public String getDiscriminatorType() {
		return discriminatorType;
	}

	/**
	 * @param discriminatorType the discriminatorType to set
	 */
	public void setDiscriminatorType(String discriminatorType) {
		this.discriminatorType = discriminatorType;
	}

	/**
	 * Indicates whether or not this {@link ObjectDescriptor} has
	 * sub classes that are mapped via a discriminator.
	 * @return
	 */
	public boolean hasSubClasses() {
		return !subClassObjectDescriptors.isEmpty();
	}

	/**
	 * Adds a sub ObjectDescriptor for the given discriminator.
	 * @param discriminator the discriminator
	 * @param descriptor the descriptor
	 */
	public void addSubClassObjectDescriptor(Object discriminator, ObjectDescriptor descriptor) {
		if (type==null) {
			throw new MjormException(
				"ObjectDescriptors without a type cann't have subclass ObjectDescriptors");
		} else if (subClassObjectDescriptors.containsKey(discriminator)) {
			throw new MjormException(
				"ObjectDescriptor for discriminator "+discriminator+" already exists");
		} else if (!type.isAssignableFrom(descriptor.getType())) {
			throw new MjormException(
				"ObjectDescriptor for discriminator "+discriminator
				+" does not inherit from "+descriptor.getType());
		}
		subClassObjectDescriptors.put(discriminator, descriptor);
	}

	/**
	 * Returns the {@link ObjectDescriptor} for the given discriminator
	 * @param discriminator the discriminator
	 * @return the {@link ObjectDescriptor}
	 */
	public ObjectDescriptor getSubClassObjectDescriptor(Object discriminator) {
		return subClassObjectDescriptors.get(discriminator);
	}

	/**
	 * Returns the {@link PropertyDescriptor} for the given
	 * {@code propertyName}.
	 * @param propertyName the name
	 * @return the {@link PropertyDescriptor}
	 */
	public PropertyDescriptor getPropertyDescriptor(String propertyName) {
		return properties.get(propertyName.toLowerCase());
	}

	/**
	 * @return the properties
	 */
	public PropertyDescriptor[] getProperties() {
		return properties.values().toArray(new PropertyDescriptor[0]);
	}

	/**
	 * Adds a {@link PropertyDescriptor}.
	 * @param desc the {@link PropertyDescriptor}
	 */
	public void addPropertyDescriptor(PropertyDescriptor desc) {
		if (properties.containsKey(desc.getName().toLowerCase())) {
			throw new IllegalArgumentException(
				"PropertyDescriptor for "+desc.getName()+" exists");
		}
		properties.put(desc.getName().toLowerCase(), desc);
	}

}
