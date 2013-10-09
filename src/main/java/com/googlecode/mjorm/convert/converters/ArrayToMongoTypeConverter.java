package com.googlecode.mjorm.convert.converters;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBList;

public class ArrayToMongoTypeConverter
	implements TypeConverter<Object[], BasicDBList> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return !BasicDBList.class.isAssignableFrom(sourceClass)
			&& BasicDBList.class.equals(targetClass)
			&& sourceClass.isArray();
	}

	public BasicDBList convert(
		Object[] source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		// create array
		BasicDBList ret = new BasicDBList();

		// iterate and convert
		for (int i=0; i<source.length; i++) {
			Object value = source[i];
			if (value!=null) {
				JavaType storageType = context.getStorageType(value.getClass());
				value = context.convert(value, storageType);
			}
			ret.add(value);
		}

		// return it
		return ret;
	}

}
