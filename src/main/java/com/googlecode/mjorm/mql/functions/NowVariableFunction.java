package com.googlecode.mjorm.mql.functions;

import java.util.Date;

import com.googlecode.mjorm.mql.AbstractMqlVariableFunction;

public abstract class NowVariableFunction
	extends AbstractMqlVariableFunction {

	public static final NowVariableFunction INSTANCE = createFunction("now");

	public static NowVariableFunction createFunction(final String name) {
		return new NowVariableFunction() {
			@Override
			protected void init() {
				setExactArgs(0);
				setFunctionName(name);
			}
		};
	}

	@Override
	protected Object doInvoke(Object[] values) {
		return new Date();
	}

}
