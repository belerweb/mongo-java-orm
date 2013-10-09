package com.googlecode.mjorm.annotations;

import java.util.Collection;

import com.googlecode.mjorm.DescriptorObjectMapper;
import com.googlecode.mjorm.ObjectDescriptor;

/**
 * Implementation of the {@ObjectMapper} that uses
 * annotations to define mappings.
 */
public class AnnotationsDescriptorObjectMapper
	extends DescriptorObjectMapper {

	private AnnotationsObjectDescriptorParser parser
		= new AnnotationsObjectDescriptorParser();

	/**
	 * Adds an annotated class to the {@link ObjectMapper}
	 * @param clazz the class to add
	 */
	public void addClass(Class<?> clazz) {
		assimilateObjectDescriptors(parser.parseClasses(clazz));
	}

	/**
	 * Assimilates {@link ObjectDescriptor}s.
	 * @param descriptors the {@link ObjectDescriptor}s
	 */
	private void assimilateObjectDescriptors(Collection<ObjectDescriptor> descriptors) {
		for (ObjectDescriptor desc : descriptors) {
			registerObjectDescriptor(desc);
		}
	}

}
