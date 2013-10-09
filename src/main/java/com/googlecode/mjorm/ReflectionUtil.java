package com.googlecode.mjorm;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utilities for reflection related things.
 */
public final class ReflectionUtil {

	/**
	 * Does nothing.
	 */
	private ReflectionUtil() { }
	static { new ReflectionUtil(); }

	/**
	 * Creates an instance of the given class.
	 * @param <T> the type
	 * @param clazz the class
	 * @param args the arguments to pass to the constructor
	 * @return an instance of the object
	 * @throws InstantiationException on error
	 * @throws IllegalAccessException on error
	 * @throws InvocationTargetException on error
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(Class<T> clazz, Object... args)
		throws InstantiationException,
		IllegalAccessException,
		InvocationTargetException {
		if (args.length==0) {
			return clazz.newInstance();
		}
		Class<?>[] types = new Class<?>[args.length];
		for (int i=0; i<args.length; i++) {
			types[i] = args.getClass();
		}
		for (Constructor<?> c : clazz.getConstructors()) {
			Class<?>[] paramTypes = c.getParameterTypes();
			if (paramTypes.length!=args.length) {
				continue;
			}
			boolean foundCtr = true;
			for (int i=0; i<paramTypes.length; i++) {
				if (!paramTypes[i].isAssignableFrom(types[i])) {
					foundCtr = false;
					break;
				}
			}
			if (foundCtr) {
				return (T)c.newInstance(args);
			}
		}
		throw new IllegalArgumentException(
			"Unable to find suitable constructor for "+clazz.getName());
	}

	/**
	 * Get {@link BeanInfo}.
	 * @param clazz the class
	 * @return the info
	 */
	public static BeanInfo getBeanInfo(Class<?> clazz) {
		try {
			return Introspector.getBeanInfo(clazz);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Returns the PropertyDescriptor for the given property.
	 * @param clazz the class
	 * @param name the getter name
	 * @return the PropertyDescriptor
	 */
	public static PropertyDescriptor findPropertyDescriptor(Class<?> clazz, String name) {
		BeanInfo info = getBeanInfo(clazz);
		for (PropertyDescriptor desc : info.getPropertyDescriptors()) {
			if (desc.getName().equalsIgnoreCase(name)) {
				return desc;
			}
		}
		return null;
	}

	/**
	 * Finds the specified getter method on the specified class.
	 * @param clazz the class
	 * @param name the getter name
	 * @return the method
	 */
	public static Method findGetter(Class<?> clazz, String name) {
		PropertyDescriptor pd = findPropertyDescriptor(clazz, name);
		return (pd!=null) ? pd.getReadMethod() : null;
	}

	/**
	 * Finds the specified setter method on the specified class.
	 * @param clazz the class
	 * @param name the setter name
	 * @return the method
	 */
	public static Method findSetter(Class<?> clazz, String name) {
		PropertyDescriptor pd = findPropertyDescriptor(clazz, name);
		return (pd!=null) ? pd.getWriteMethod() : null;
	}

}
