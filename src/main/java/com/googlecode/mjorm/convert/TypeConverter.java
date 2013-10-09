package com.googlecode.mjorm.convert;

public interface TypeConverter<S, T> {

	boolean canConvert(Class<?> sourceClass, Class<?> targetClass);

	T convert(S source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException;

}
