package com.googlecode.mjorm.mql;

public interface MqlVariableFunction {

	Object invoke(Object[] args);

	String getName();

}
