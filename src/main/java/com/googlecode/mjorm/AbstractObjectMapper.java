package com.googlecode.mjorm;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConverter;
import com.googlecode.mjorm.convert.converters.ArrayToMongoTypeConverter;
import com.googlecode.mjorm.convert.converters.CollectionToMongoTypeConverter;
import com.googlecode.mjorm.convert.converters.BooleanTypeConverter;
import com.googlecode.mjorm.convert.converters.CharacterTypeConverter;
import com.googlecode.mjorm.convert.converters.EnumToMongoTypeConverter;
import com.googlecode.mjorm.convert.converters.MapToMongoTypeConverter;
import com.googlecode.mjorm.convert.converters.MongoToArrayTypeConverter;
import com.googlecode.mjorm.convert.converters.MongoToCollectionTypeConverter;
import com.googlecode.mjorm.convert.converters.MongoToEnumTypeConverter;
import com.googlecode.mjorm.convert.converters.MongoToMapTypeConverter;
import com.googlecode.mjorm.convert.converters.NumberTypeConverter;
import com.googlecode.mjorm.convert.converters.ObjectIdToStringTypeConverter;
import com.googlecode.mjorm.convert.converters.StringToObjectIdTypeConverter;
import com.googlecode.mjorm.convert.converters.StringToUUIDConverter;
import com.googlecode.mjorm.convert.converters.StringTypeConverter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Abstract {@link ObjectMapper} that handles most of the work of
 * object conversion for the subclass.
 */
public abstract class AbstractObjectMapper
	implements ObjectMapper {

	private ConversionContext conversionContext = new ConversionContext();

	/**
	 * Creates the mapper.
	 */
	public AbstractObjectMapper() {
		registerTypeConverter(new StringTypeConverter());
		registerTypeConverter(new NumberTypeConverter());
		registerTypeConverter(new CharacterTypeConverter());
		registerTypeConverter(new BooleanTypeConverter());
		registerTypeConverter(new ObjectIdToStringTypeConverter());
		registerTypeConverter(new StringToObjectIdTypeConverter());
		registerTypeConverter(new StringToUUIDConverter());
		registerTypeConverter(new ArrayToMongoTypeConverter());
		registerTypeConverter(new CollectionToMongoTypeConverter());
		registerTypeConverter(new EnumToMongoTypeConverter());
		registerTypeConverter(new MapToMongoTypeConverter());
		registerTypeConverter(new MongoToArrayTypeConverter());
		registerTypeConverter(new MongoToCollectionTypeConverter());
		registerTypeConverter(new MongoToMapTypeConverter());
		registerTypeConverter(new MongoToEnumTypeConverter());
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T map(DBObject dbObject, Class<T> objectClass) {
		try {
			return conversionContext.convert(dbObject, JavaType.fromType(objectClass));
		} catch(ConversionException ce) {
			throw new MjormException(ce);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> DBObject unmap(T object) {
		try {
			return conversionContext.convert(object, JavaType.fromType(BasicDBObject.class));
		} catch(ConversionException ce) {
			throw new MjormException(ce);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> Object unmapValue(T object) throws MjormException {
		try {
			if (object==null) {
				return null;
			}
			JavaType storageType = conversionContext.getStorageType(object.getClass());
			return conversionContext.convert(object, storageType);
		} catch(ConversionException ce) {
			throw new MjormException(ce);
		}
	}

	/**
	 * Registers the given {@link TypeConverter}.
	 * @param typeConverter the {@link TypeConverter}
	 */
	public void registerTypeConverter(TypeConverter<?, ?> typeConverter) {
		conversionContext.registerTypeConverter(typeConverter);
	}

}
