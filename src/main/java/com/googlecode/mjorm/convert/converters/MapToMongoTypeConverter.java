package com.googlecode.mjorm.convert.converters;

import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBObject;

public class MapToMongoTypeConverter
	implements TypeConverter<Map<String, Object>, BasicDBObject> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return !BasicDBObject.class.isAssignableFrom(sourceClass)
			&& Map.class.isAssignableFrom(sourceClass)
			&& BasicDBObject.class.equals(targetClass);
	}

	public BasicDBObject convert(
		Map<String, Object> source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		// convert
		BasicDBObject ret = new BasicDBObject();
		for (Entry<String, Object> entry : source.entrySet()) {
			Object value = entry.getValue();
			
			if (value!=null) {

				// get storage type
				JavaType storageType = context.getStorageType(value.getClass());

				// convert
				value = context.convert(entry.getValue(), storageType);
			}
			
			ret.put(entry.getKey(), value);
		}
		return ret;
	}

}
