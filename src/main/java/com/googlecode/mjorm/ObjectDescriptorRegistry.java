package com.googlecode.mjorm;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A registry for {@link ObjectDescriptor}s.
 */
public class ObjectDescriptorRegistry {

	private Map<Class<?>, ObjectDescriptor> descriptors
		= new HashMap<Class<?>, ObjectDescriptor>();

	/**
	 * Registers an {@link ObjectDescriptor}.
	 * @param descriptor the {@link ObjectDescriptor}
	 */
	public void registerDescriptor(ObjectDescriptor descriptor) {
		if (descriptors.containsKey(descriptor.getType())) {
			throw new IllegalStateException(
				"ObjectDescriptor for "+descriptor.getType().getName()+" exists");
		}
		descriptors.put(descriptor.getType(), descriptor);
	}

	/**
	 * @param clazz the clazz
	 * @return
	 */
	public boolean hasDescriptor(Class<?> clazz) {
		return descriptors.containsKey(clazz);
	}

	/**
	 * @param type the type
	 * @return
	 */
	public boolean hasDescriptor(Type type) {
		if (Class.class.isInstance(type)) {
			return hasDescriptor(Class.class.cast(type));
		}
		return false;
	}

	/**
	 * @param clazz the clazz
	 * @return
	 */
	public ObjectDescriptor getDescriptor(Class<?> clazz) {
		return descriptors.get(clazz);
	}

	/**
	 * @param type the type
	 * @return
	 */
	public ObjectDescriptor getDescriptor(Type type) {
		if (Class.class.isInstance(type)) {
			return getDescriptor(Class.class.cast(type));
		}
		return null;
	}

	/**
	 * Returns a {@link List} of {@link ObjectDescriptor}s that describe
	 * how to map to and from the given type and it's class hieararchy.
	 * @param type the type
	 * @return the {@link List}
	 */
	public LinkedList<ObjectDescriptor> getDescriptorsForType(Type type) {
		List<Type> hierarchy = getClassHierarchy(type);
		LinkedList<ObjectDescriptor> ret = new LinkedList<ObjectDescriptor>();
		for (Type clazz : hierarchy) {
			ObjectDescriptor desc = getDescriptor(clazz);
			if (desc!=null) {
				ret.add(desc);
			}
		}
		return ret;
	}

	/**
	 * Returns a classes' hierarchy in superclass to subclass order.
	 * @param type the type
	 * @return the hierarchy.
	 */
	private LinkedList<Type> getClassHierarchy(Type type) {
		LinkedList<Type> ret = new LinkedList<Type>();
		while (type!=null && !Object.class.equals(type)) {
			ret.add(type);
			if (Class.class.isInstance(type)) {
				type = Class.class.cast(type).getSuperclass();
			}
		}
		Collections.reverse(ret);
		return ret;
	}

}
