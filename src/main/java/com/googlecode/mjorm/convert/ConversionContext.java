package com.googlecode.mjorm.convert;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class ConversionContext {

	private Collection<TypeConverter<?, ?>> typeConverters
		= new LinkedList<TypeConverter<?, ?>>();
	private Map<String, TypeConverter<?, ?>> typeConverterCache
		= new HashMap<String, TypeConverter<?, ?>>();
	private Map<Class<?>, Class<?>> storageClasses
		= new HashMap<Class<?>, Class<?>>();

	/**
	 * Returns the storage class for the given class.
	 * @param clazz the class
	 * @return the default storage class
	 * @throws ConversionException on error
	 */
	public JavaType getStorageType(Class<?> clazz)
		throws ConversionException {
		Class<?> ret = storageClasses.get(clazz);
		if (ret==null) {
			if (clazz.isPrimitive()) {
				ret = clazz;
			} else if (String.class.isAssignableFrom(clazz)) {
				ret = clazz;
			} else if (Number.class.isAssignableFrom(clazz)) {
				ret = clazz;
			} else if (Boolean.class.isAssignableFrom(clazz)) {
				ret = clazz;
			} else if (Character.class.isAssignableFrom(clazz)) {
				ret = clazz;
			} else if (Date.class.isAssignableFrom(clazz)) {
				ret = clazz;
			} else if (ObjectId.class.equals(clazz)) {
				ret = clazz;
			} else if (UUID.class.equals(clazz)) {
				ret = String.class;
			} else if (clazz.isArray()) {
				ret = BasicDBList.class;
			} else if (Collection.class.isAssignableFrom(clazz)) {
				ret = BasicDBList.class;
			} else if (Map.class.isAssignableFrom(clazz)) {
				ret = BasicDBObject.class;
			} else if (Enum.class.isAssignableFrom(clazz)) {
				ret = String.class;
			} else { // default to this
				ret = BasicDBObject.class;
			}
		}
		return JavaType.fromType(ret);
	}

	public <S, T> T convert(S source, JavaType targetType)
		throws ConversionException {
		return convert(source, targetType, TypeConversionHints.NO_HINTS);
	}

	@SuppressWarnings("unchecked")
	public <S, T> T convert(S source, JavaType targetType, TypeConversionHints hints)
		throws ConversionException {

		// must have hints
		if (hints==null) {
			throw new IllegalArgumentException(
				"Must have TypeConversionHints, consider TypeConversionHints.NO_HINTS or "
				+"the variation of convert() that doesn't have a TypeConversionHints parameter");
		}

		// pass nulls through
		if (source==null) { return null; }

		// bail on null target
		if (targetType==null) {
			throw new IllegalArgumentException(
				"Must have a targetType and it must be instantiable");
		}

		// get source class
		Class<?> sourceClass = source.getClass();

		// no conversion needed
		if (sourceClass.equals(targetType.asClass())) {
			return (T)source;
		}

		// find a converter
		TypeConverter<S, T> conv = getConverter(sourceClass, targetType.asClass());
		if (conv==null) {
			throw new ConversionException(
				"Unable to map "+sourceClass+" to "+targetType);
		}

		// do the conversion
		return (T)conv.convert(source, targetType, this, hints);
	}

	/**
	 * Returns a TypeConverter capable of converting source to target.
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S, T> TypeConverter<S, T> getConverter(Class<?> sourceClass, Class<?> targetClass) {
		String cacheKey = sourceClass.getName()+"_"+targetClass.getName();
		if (typeConverterCache.containsKey(cacheKey)) {
			return (TypeConverter<S, T>)typeConverterCache.get(cacheKey);
		}
		for (TypeConverter<?, ?> conv : typeConverters) {
			if (conv.canConvert(sourceClass, targetClass)) {
				typeConverterCache.put(cacheKey, conv);
				return (TypeConverter<S, T>) conv;
			}
		}
		return null;
	}


	/**
	 * Registers a new {@link TypeConverter}.
	 * @param typeConverter the converter
	 */
	public void registerTypeConverter(TypeConverter<?, ?> typeConverter) {
		typeConverterCache.clear();
		typeConverters.add(typeConverter);
	}

}
