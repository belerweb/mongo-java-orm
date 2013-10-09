package com.googlecode.mjorm.convert.converters;

import java.util.UUID;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class StringToUUIDConverter
	implements TypeConverter<Object, UUID> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return (String.class.equals(sourceClass) || byte[].class.isAssignableFrom(sourceClass))
			&& UUID.class.equals(targetClass);
	}

	public UUID convert(Object source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {
		if (targetType.is(byte[].class) || targetType.is(Byte[].class)) {
			return UUID.nameUUIDFromBytes(byte[].class.cast(source));
		} else {
			return UUID.fromString(String.class.cast(source));
		}
	}

}
