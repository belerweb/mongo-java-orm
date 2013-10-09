package com.googlecode.mjorm.mql.functions;

import org.bson.types.ObjectId;

import com.googlecode.mjorm.mql.AbstractMqlVariableFunction;

public abstract class ObjectIdVariableFunction
	extends AbstractMqlVariableFunction {

	public static final ObjectIdVariableFunction INSTANCE = createFunction("object_id");

	public static ObjectIdVariableFunction createFunction(final String name) {
		return new ObjectIdVariableFunction() {
			@Override
			protected void init() {
				setFunctionName(name);
				setExactArgs(1);
				setTypes(String.class);
			}
		};
	}

	@Override
	protected Object doInvoke(Object[] values) {
		return new ObjectId(values[0].toString());
	}

}
