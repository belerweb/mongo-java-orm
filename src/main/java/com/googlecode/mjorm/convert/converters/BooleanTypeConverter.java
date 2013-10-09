package com.googlecode.mjorm.convert.converters;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class BooleanTypeConverter
	implements TypeConverter<Object, Boolean> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return (Number.class.isAssignableFrom(sourceClass)
			|| Character.class.isAssignableFrom(sourceClass)
			|| String.class.isAssignableFrom(sourceClass)
		) && Boolean.class.equals(targetClass);
	}

	public Boolean convert(
		Object source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		if (Number.class.isInstance(source)) {
			return Boolean.valueOf(Number.class.cast(source).intValue()==1);

		} else if (Character.class.isInstance(source)) {
			char c = Character.class.cast(source).charValue();
			return Boolean.valueOf(c==(char)1);

		} else if (String.class.isInstance(source)) {
			return Boolean.valueOf(String.class.cast(source));
		}

		throw new ConversionException("Unable to convert source to boolean");
	}

}
