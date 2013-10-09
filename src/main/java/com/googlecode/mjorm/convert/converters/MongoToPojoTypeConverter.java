package com.googlecode.mjorm.convert.converters;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.googlecode.mjorm.DBObjectUtil;
import com.googlecode.mjorm.MjormException;
import com.googlecode.mjorm.ObjectDescriptor;
import com.googlecode.mjorm.ObjectDescriptorRegistry;
import com.googlecode.mjorm.PropertyDescriptor;
import com.googlecode.mjorm.ReflectionUtil;
import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBObject;

public class MongoToPojoTypeConverter
	implements TypeConverter<BasicDBObject, Object> {

	private ObjectDescriptorRegistry registry;

	public MongoToPojoTypeConverter(ObjectDescriptorRegistry registry) {
		this.registry = registry;
	}

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return registry.hasDescriptor(targetClass)
			&& BasicDBObject.class.equals(sourceClass);
	}

	public Object convert(
		BasicDBObject source, JavaType targetType, ConversionContext context, TypeConversionHints hints)
		throws ConversionException {

		// get the target class
		Class<?> targetClass = targetType.asClass();

		// get the descriptors
		LinkedList<ObjectDescriptor> descriptors = registry.getDescriptorsForType(targetClass);
		if (descriptors.isEmpty()) {
			throw new MjormException("Unable to find ObjectDescriptor for "+targetClass);
		}

		// get descriptor
		ObjectDescriptor descriptor = descriptors.get(descriptors.size()-1);

		// get the discriminator name
		String discriminatorName = descriptor.getDiscriminatorName();
		Object discriminator = null;
		if (discriminatorName!=null && discriminatorName.trim().length()>0) {
			discriminator = DBObjectUtil.getNestedProperty(source, discriminatorName);
		}

		// if we have a discriminiator - figure out which
		// subclass to use
		if (discriminator!=null) {
			ObjectDescriptor subClass = descriptor.getSubClassObjectDescriptor(discriminator);
			if (subClass==null && Modifier.isAbstract(descriptor.getType().getModifiers())) {
				throw new MjormException(
					"Sublcass for discriminiator value "+discriminator
					+" was not found on abstract ObjectDescriptor for "
					+ descriptor.getType().getName());
			} else if (subClass!=null) {
				descriptor = subClass;
				descriptors.addFirst(subClass);
			}
		}

		// create the return object
		Object ret;
		try {
			ret = ReflectionUtil.instantiate(descriptor.getType());
		} catch (Exception e) {
			throw new MjormException(
				"Error creating class: "+targetClass, e);
		}

		// loop through each descriptor
		for (ObjectDescriptor desc : descriptors) {
	
			// loop through each property
			for (PropertyDescriptor prop : desc.getProperties()) {
	
				try {

					// the field name
					String fieldName = prop.isIdentifier() ? "_id" : prop.getFieldName();

					// get the value
					Object value = source.get(fieldName);

					// convert
					if (value!=null) {
						// setup hints
						TypeConversionHints nextHints = new TypeConversionHints();
						if (prop.getConversionHints()!=null && !prop.getConversionHints().isEmpty()) {
							for (Entry<String, Object> entry : prop.getConversionHints().entrySet()) {
								nextHints.set(entry.getKey(), entry.getValue());
							}
						}

						// add generic type parameter hints
						Type[] genericParameterTypes = prop.getGenericParameterTypes();
						if (genericParameterTypes!=null && genericParameterTypes.length>0) {
							nextHints.set(TypeConversionHints.HINT_GENERIC_TYPE_PARAMETERS, genericParameterTypes);
						}
						
						// convert
						value = context.convert(value, prop.getType(), nextHints);
					}

					// set
					prop.set(ret, value);
	
				} catch (Exception e) {
					throw new MjormException(
						"Error mapping property "+prop.getName()
						+" of class "+descriptor.getType(), e);
				}
	
			}
		}

		// return it
		return ret;
	}

}
