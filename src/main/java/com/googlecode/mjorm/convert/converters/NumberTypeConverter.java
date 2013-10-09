package com.googlecode.mjorm.convert.converters;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class NumberTypeConverter
	implements TypeConverter<Number, Number> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return (Number.class.isAssignableFrom(sourceClass)
				|| byte.class.isAssignableFrom(targetClass)
				|| short.class.isAssignableFrom(targetClass)
				|| int.class.isAssignableFrom(targetClass)
				|| long.class.isAssignableFrom(targetClass)
				|| float.class.isAssignableFrom(targetClass)
				|| double.class.isAssignableFrom(targetClass))
			&& (Number.class.isAssignableFrom(targetClass)
				|| byte.class.isAssignableFrom(targetClass)
				|| short.class.isAssignableFrom(targetClass)
				|| int.class.isAssignableFrom(targetClass)
				|| long.class.isAssignableFrom(targetClass)
				|| float.class.isAssignableFrom(targetClass)
				|| double.class.isAssignableFrom(targetClass));
	}

	public Number convert(Number source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		if (targetType.is(Byte.class) || targetType.is(byte.class)) {
			return Byte.valueOf(source.byteValue());
			
		} else if (targetType.is(Short.class) || targetType.is(short.class)) {
			return Short.valueOf(source.shortValue());
			
		} else if (targetType.is(Integer.class) || targetType.is(int.class)) {
			return Integer.valueOf(source.intValue());
			
		} else if (targetType.is(Long.class) || targetType.is(long.class)) {
			return Long.valueOf(source.longValue());
			
		} else if (targetType.is(Float.class) || targetType.is(float.class)) {
			return Float.valueOf(source.floatValue());
			
		} else if (targetType.is(Double.class) || targetType.is(double.class)) {
			return Double.valueOf(source.doubleValue());
			
		} else if (targetType.is(BigDecimal.class)) {
			return BigDecimal.valueOf(source.floatValue());
			
		} else if (targetType.is(BigInteger.class)) {
			return BigInteger.valueOf(source.longValue());
			
		}

		throw new ConversionException(
			"Unable to convert source Number to "+targetType);
	}

}
