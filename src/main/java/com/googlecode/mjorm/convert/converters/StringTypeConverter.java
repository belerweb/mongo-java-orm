package com.googlecode.mjorm.convert.converters;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class StringTypeConverter
	implements TypeConverter<Object, String> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return String.class.isAssignableFrom(targetClass);
	}

	public String convert(Object source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {
		return source.toString();
	}

}
