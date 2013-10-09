package com.googlecode.mjorm.mql.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.mjorm.mql.AbstractMqlVariableFunction;
import com.googlecode.mjorm.mql.MqlException;

public abstract class DateVariableFunction
	extends AbstractMqlVariableFunction {

	public static final String[] DATE_TIME_FORMATS = {
		"yyyy-MM-dd HH:mm:ss,S z",
		"yyyy-MM-dd HH:mm:ss,S",
		"yyyy-MM-dd HH:mm:ss z",
		"yyyy-MM-dd HH:mm:ss",
		"yyyy-MM-dd z",
		"yyyy-MM-dd",
	};

	public static final DateVariableFunction INSTANCE = DateVariableFunction.createFunction("date", DATE_TIME_FORMATS);

	public static DateVariableFunction createFunction(final String name, final String... formats) {
		return new DateVariableFunction() {
			@Override
			protected void init() {
				setFunctionName(name);
				setExactArgs(1);
				setFormats(formats);
			}
		};
	}

	private SimpleDateFormat[] formats;

	protected void setFormats(String[] formats) {
		assertNotInitialized();
		this.formats = new SimpleDateFormat[formats.length];
		for (int i=0; i<formats.length; i++) {
			this.formats[i] = new SimpleDateFormat(formats[i]);
		}
	}

	@Override
	protected Object doInvoke(Object[] values) {
		if (values==null || values.length==0) {
			return new Date();
		}
		return parseDate(values[0]);
	}

	private Date parseDate(Object value) {
		if (Number.class.isInstance(value)) {
			return new Date(Number.class.cast(value).longValue());
		} else if (String.class.isInstance(value)) {
			String str = String.class.cast(value);
			for (int i=0; i<this.formats.length; i++) {
				try {
					return this.formats[i].parse(str);
				} catch (ParseException e) {
					// skip this format
				}
			}
			throw new MqlException("Unable to parse "+str);
		}
		throw new MqlException("Unknown input object type: "+value.getClass().getName());
	}

}
