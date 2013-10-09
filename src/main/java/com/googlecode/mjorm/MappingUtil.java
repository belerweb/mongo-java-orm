package com.googlecode.mjorm;

/**
 * Various utilities for mapping.
 *
 */
public class MappingUtil {

	/**
	 * Parses a discriminator.
	 * @param value the value
	 * @param type the type
	 * @return the parsed value
	 */
	public static Object parseDiscriminator(String value, DiscriminatorType type) {
		try {
			switch(type) {
				case BOOLEAN: return Boolean.parseBoolean(value);
				case CHARACTER: return new Character(value.charAt(0));
				case DOUBLE: return new Double(value);
				case FLOAT: return new Float(value);
				case LONG: return new Long(value);
				case INTEGER: return new Integer(value);
				case SHORT: return new Short(value);
				case STRING: return value;
			}
		} catch(Exception e) {
			throw new MjormException("Error parsing discrininator", e);
		}
		throw new MjormException(
			"Error determining discriminator type for "+type);
	}

	/**
	 * Parses a discriminator.
	 * @param value the value
	 * @param type the type
	 * @return the parsed value
	 */
	public static Object parseDiscriminator(String value, String type) {
		return parseDiscriminator(value, DiscriminatorType.valueOf(type.toUpperCase()));
	}

}
