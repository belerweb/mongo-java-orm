package com.googlecode.mjorm.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.googlecode.mjorm.convert.TypeConverter;

/**
 * {@link FactoryBean} for created {@link AnnotationsDescriptorObjectMapper}s.
 */
public class AnnotationsDescriptorObjectMapperFactoryBean
	extends AbstractFactoryBean<AnnotationsDescriptorObjectMapper> {

	private Class<?>[] annotatedClasses = new Class<?>[0];
	private List<TypeConverter<?, ?>> typeConverters = new ArrayList<TypeConverter<?, ?>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AnnotationsDescriptorObjectMapper createInstance()
		throws Exception {
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		for (TypeConverter<?, ?> converter : typeConverters) {
			mapper.registerTypeConverter(converter);
		}
		for (Class<?> clazz : annotatedClasses) {
			mapper.addClass(clazz);
		}
		return mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getObjectType() {
		return AnnotationsDescriptorObjectMapper.class;
	}

	/**
	 * @param typeConverters the typeConverters to set
	 */
	public void setTypeConverters(List<TypeConverter<?, ?>> typeConverters) {
		this.typeConverters = typeConverters;
	}

	/**
	 * @param annotatedClasses the annotatedClasses to set
	 */
	protected void setAnnotatedClasses(Class<?>[] annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

}
