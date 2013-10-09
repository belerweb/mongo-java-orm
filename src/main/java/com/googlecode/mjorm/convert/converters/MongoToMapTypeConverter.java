package com.googlecode.mjorm.convert.converters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBObject;

public class MongoToMapTypeConverter
	implements TypeConverter<BasicDBObject, Map<String, Object>> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return BasicDBObject.class.equals(sourceClass)
			&& Map.class.isAssignableFrom(targetClass);
	}

	public Map<String, Object> convert(
		BasicDBObject source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		// get parameter types
		JavaType parameterType = null;
		Type[] types = hints.get(TypeConversionHints.HINT_GENERIC_TYPE_PARAMETERS);
		if (types!=null && types.length>1) {
			parameterType = JavaType.fromType(types[1]);
		}
		if (parameterType==null) {
			parameterType = targetType.getJavaTypeParameter(1);
		}

		// bail if we don't have a parameter type
		if (parameterType==null) {
			throw new ConversionException(
				"Unable to determine parameter type for "+targetType);
		}

		// create and convert
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Entry<String, Object> entry : source.entrySet()) {

			// get value
			Object value = entry.getValue();

			// convert
			if (value!=null) {
				value = context.convert(value, parameterType);
			}

			ret.put(entry.getKey(), value);
		}
		return ret;
	}

}
