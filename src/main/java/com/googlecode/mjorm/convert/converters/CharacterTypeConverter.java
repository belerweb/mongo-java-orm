package com.googlecode.mjorm.convert.converters;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class CharacterTypeConverter
	implements TypeConverter<Object, Character> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return (Number.class.isAssignableFrom(sourceClass)
			|| String.class.isAssignableFrom(sourceClass)
			|| Boolean.class.isAssignableFrom(sourceClass)
		) && Character.class.equals(targetClass);
	}


	public Character convert(Object source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		if (Number.class.isInstance(source)) {
			return Character.valueOf((char)Number.class.cast(source).intValue());
			
		} else if (Boolean.class.isInstance(source)) {
			boolean b = Boolean.class.cast(source);
			return Character.valueOf(b ? (char)1 : (char)0);
			
		} else if (String.class.isInstance(source)) {
			return Character.valueOf(String.class.cast(source).trim().charAt(0));
			
		}
		throw new ConversionException("Unable to convert source to character");
	}

}
