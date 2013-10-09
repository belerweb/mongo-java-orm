package com.googlecode.mjorm.convert.converters;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBList;

public class MongoToArrayTypeConverter
	implements TypeConverter<BasicDBList, Object[]> {

	public boolean canConvert(Class<?> dbClass, Class<?> targetClass) {
		return BasicDBList.class.equals(dbClass)
			&& targetClass.isArray();
	}

	public Object[] convert(
		BasicDBList source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		// get component type of array
		JavaType componentType = null;
		Type[] types = hints.get(TypeConversionHints.HINT_GENERIC_TYPE_PARAMETERS);
		if (types!=null && types.length>0) {
			componentType = JavaType.fromType(types[0]);
		}
		if (componentType==null) {
			componentType = targetType.getComponentJavaType();
		}

		// bail if we don't have a component type
		if (componentType==null) {
			throw new ConversionException(
				"Unable to determine componentType of "+targetType);
		}

		// create array
		Object ret = Array.newInstance(componentType.asClass(), source.size());

		// iterate and convert
		for (int i=0; i<source.size(); i++) {
			Object value = source.get(i);
			if (value!=null) {
				value = context.convert(value, componentType);
			}
			Array.set(ret, i, value);
		}

		// return it
		return Object[].class.cast(ret);
	}

}
