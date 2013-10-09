package com.googlecode.mjorm.spring;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import com.googlecode.mjorm.XmlDescriptorObjectMapper;
import com.googlecode.mjorm.convert.TypeConverter;

/**
 * {@link FactoryBean} for created {@link XmlDescriptorObjectMapper}s.
 */
public class XmlDescriptorObjectMapperFactoryBean
	extends AbstractFactoryBean<XmlDescriptorObjectMapper> {

	private List<Resource> xmlResources = new ArrayList<Resource>();
	private List<File> xmlFiles = new ArrayList<File>();
	private List<TypeConverter<?, ?>> typeConverters = new ArrayList<TypeConverter<?, ?>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected XmlDescriptorObjectMapper createInstance()
		throws Exception {
		XmlDescriptorObjectMapper mapper = new XmlDescriptorObjectMapper();
		for (TypeConverter<?, ?> converter : typeConverters) {
			mapper.registerTypeConverter(converter);
		}
		for (Resource resource : xmlResources) {
			InputStream ips = resource.getInputStream();
			mapper.addXmlObjectDescriptor(ips);
			ips.close();
		}
		for (File file : xmlFiles) {
			mapper.addXmlObjectDescriptor(file);
		}
		return mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getObjectType() {
		return XmlDescriptorObjectMapper.class;
	}

	/**
	 * @param xmlResources the xmlResources to set
	 */
	public void setXmlResources(List<Resource> xmlResources) {
		this.xmlResources = xmlResources;
	}

	/**
	 * @param xmlFiles the xmlFiles to set
	 */
	public void setXmlFiles(List<File> xmlFiles) {
		this.xmlFiles = xmlFiles;
	}

	/**
	 * @param typeConverters the typeConverters to set
	 */
	public void setTypeConverters(List<TypeConverter<?, ?>> typeConverters) {
		this.typeConverters = typeConverters;
	}

}
