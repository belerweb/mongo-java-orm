package com.googlecode.mjorm.convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for working with java types.
 */
public class JavaType {

	private static final Map<Type, JavaType> CACHE = new HashMap<Type, JavaType>();

	private Type type;
	private ParameterizedType parameterizedType;
	private Class<?> clazz;
	private Class<?> componentType;
	private Type[] typeParameters;
	private boolean instantiable;
	private boolean genericInfo;

	public static JavaType[] fromTypes(Type... types) {
		JavaType[] ret = new JavaType[types.length];
		for (int i=0; i<types.length; i++) {
			ret[i] = JavaType.fromType(types[i]);
		}
		return ret;
	}

	/**
	 * Returns the {@link JavaType} for the given {@link Type}.
	 * @param type
	 * @return
	 */
	public static JavaType fromType(Type type) {
		if (type==null) {
			throw new IllegalArgumentException("Type can't be null");
		}
		if (!CACHE.containsKey(type)) {
			CACHE.put(type, new JavaType(type));
		}
		return CACHE.get(type);
	}

	/**
	 * Creates the type, it's private so you must use fromType.
	 * @param type
	 */
	private JavaType(Type type) {

		// the type
		this.type = type;

		// parameterized?
		parameterizedType = ParameterizedType.class.isInstance(type)
			? ParameterizedType.class.cast(type) : null;

		// generic info
		genericInfo = parameterizedType!=null;

		// get class
		if (Class.class.isInstance(type)) {
			clazz = Class.class.cast(type);
		} else if (parameterizedType!=null) {
			clazz = Class.class.cast(parameterizedType.getRawType());
		} else {
			clazz = null;
		}

		// component type
		if (clazz!=null && clazz.getComponentType()!=null) {
			componentType = clazz.getComponentType();
		}

		// instantiable
		instantiable = clazz != null;

		// typeParameters
		typeParameters = parameterizedType!=null
			? parameterizedType.getActualTypeArguments()
			: null;
	
		// genericInfo
		genericInfo = typeParameters != null;
	}

	public boolean is(Type type) {
		return type!=null ? this.type.equals(type) : false;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the parameterizedType
	 */
	public ParameterizedType asParameterizedType() {
		return parameterizedType;
	}

	/**
	 * @return the clazz
	 */
	public Class<?> asClass() {
		return clazz;
	}

	/**
	 * @return the typeParameters
	 */
	public Type[] getTypeParameters() {
		return typeParameters;
	}

	/**
	 * @return the typeParameters
	 */
	public Type getTypeParameter(int index) {
		return typeParameters!=null && typeParameters.length>index
			? typeParameters[index] : null;
	}

	/**
	 * @return the typeParameters
	 */
	public JavaType getJavaTypeParameter(int index) {
		Type type = getTypeParameter(index);
		return (type==null) ? null : JavaType.fromType(type);
	}

	/**
	 * @return the instantiable
	 */
	public boolean isInstantiable() {
		return instantiable;
	}

	/**
	 * @return the genericInfo
	 */
	public boolean hasGenericInfo() {
		return genericInfo;
	}

	/**
	 * @return the componentType
	 */
	public Class<?> getComponentType() {
		return componentType;
	}

	/**
	 * @return the componentType
	 */
	public JavaType getComponentJavaType() {
		return (componentType!=null)
			? JavaType.fromType(componentType)
			: null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaType other = (JavaType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JavaType("+type.toString()+")";
	}

}
