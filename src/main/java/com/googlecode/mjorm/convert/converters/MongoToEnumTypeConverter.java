package com.googlecode.mjorm.convert.converters;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class MongoToEnumTypeConverter
	implements TypeConverter<String, Enum<?>> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return String.class.equals(sourceClass)
			&& Enum.class.isAssignableFrom(targetClass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enum<?> convert(
		String source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {
		return Enum.valueOf((Class<Enum>)targetType.asClass(), source);
	}

}
