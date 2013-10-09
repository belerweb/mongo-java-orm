package com.googlecode.mjorm.convert.converters;

import org.bson.types.ObjectId;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class ObjectIdToStringTypeConverter
	implements TypeConverter<ObjectId, String> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return ObjectId.class.equals(sourceClass)
			&& String.class.equals(targetClass);
	}

	public String convert(
		ObjectId source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {
		return source.toStringMongod();
	}

}
