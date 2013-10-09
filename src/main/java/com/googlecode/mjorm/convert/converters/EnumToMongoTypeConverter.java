package com.googlecode.mjorm.convert.converters;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class EnumToMongoTypeConverter
	implements TypeConverter<Enum<?>, String> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return Enum.class.isAssignableFrom(sourceClass)
			&& String.class.equals(targetClass);
	}

	public String convert(
		Enum<?> source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {
		return source.name();
	}

}
