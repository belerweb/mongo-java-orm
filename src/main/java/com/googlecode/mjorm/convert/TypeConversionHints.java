package com.googlecode.mjorm.convert;

import java.util.HashMap;
import java.util.Map;

public class TypeConversionHints {

	public static final String HINT_GENERIC_TYPE_PARAMETERS = "genericTypeParameters";

	public static final TypeConversionHints NO_HINTS
		= new TypeConversionHints() {
		@Override
		public void set(String key, Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public <T> T get(String key) {
			return null;
		}
	};

	private Map<String, Object> other = new HashMap<String, Object>();

	/**
	 * Sets other hints.
	 * @param key hint key
	 * @param value hint value
	 */
	public void set(String key, Object value) {
		other.put(key, value);
	}

	/**
	 * Gets an other hint.
	 * @param key the key
	 * @return the hint value
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T)other.get(key);
	}

}
