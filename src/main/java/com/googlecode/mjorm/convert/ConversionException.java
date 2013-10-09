package com.googlecode.mjorm.convert;

@SuppressWarnings("serial")
public class ConversionException
	extends Exception {

	public ConversionException(String message, Throwable t) {
		super(message, t);
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable t) {
		super(t);
	}

}
