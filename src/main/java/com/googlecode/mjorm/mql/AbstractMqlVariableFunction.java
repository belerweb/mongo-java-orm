package com.googlecode.mjorm.mql;

public abstract class AbstractMqlVariableFunction
	extends AbstractMqlFunction
	implements MqlVariableFunction {

	protected Object doInvoke(Object[] values) {
		throw new IllegalArgumentException(
			getFunctionName()+" doesn't implement doInvoke(Object[])");
	}

	public Object invoke(Object[] values) {
		assertCorrectNumberOfArguments(values);
		return doInvoke(values);
	}

}