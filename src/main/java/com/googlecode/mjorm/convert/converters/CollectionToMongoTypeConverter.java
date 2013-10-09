package com.googlecode.mjorm.convert.converters;

import java.util.Collection;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBList;

public class CollectionToMongoTypeConverter
	implements TypeConverter<Collection<?>, BasicDBList> {

	public static final String HINT_COMPARATOR_CLASS = "comparatorClass";

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return !BasicDBList.class.isAssignableFrom(sourceClass)
			&& Collection.class.isAssignableFrom(sourceClass)
			&& BasicDBList.class.equals(targetClass);
	}

	public BasicDBList convert(
		Collection<?> source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		// convert
		BasicDBList ret = new BasicDBList();
		for (Object value : source) {
			if (value!=null) {

				// get storage type
				JavaType storageType = context.getStorageType(value.getClass());

				// convert
				value = context.convert(value, storageType);
			}
			ret.add(value);
		}
		return ret;
	}

}
