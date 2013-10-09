package com.googlecode.mjorm.mql;

public abstract class AbstractMqlFunction {

	private String funcName = "unknown";
	private int exactArgs = -1;
	private int maxArgs = -1;
	private int minArgs = -1;
	private Class<?> types[] = new Class<?>[0];
	private boolean initialized = false;
	private boolean strictInitialization = true;

	protected AbstractMqlFunction() {
		initialized = false;
		init();
		if (funcName==null || funcName.trim().length()==0) {
			throw new IllegalArgumentException("Invalid function name");
		}
		initialized = true;
	}

	protected abstract void init();

	public String getName() {
		return funcName;
	}

	protected void assertNotInitialized() {
		if (strictInitialization && initialized) {
			throw new IllegalStateException("Function "+funcName+" already initialized");
		}
	}

	protected void assertArgumentTypes(Object[] arguments, Class<?> type) {
		for (int i=0; i<arguments.length; i++) {
			if (arguments[i]!=null && !type.isInstance(arguments[i])) {
				throw new IllegalArgumentException("Invalid type for argument "+i+" in function "+funcName);
			}
		}
	}

	protected void assertArgumentTypes(Object[] arguments, Class<?>[] types) {
		if (arguments.length!=types.length) {
			throw new IllegalArgumentException(
				"Argument length doesn't match type length in function "+funcName);
		}
		for (int i=0; i<types.length; i++) {
			if (!types[i].isInstance(arguments[i])) {
				throw new IllegalArgumentException(
					"Invalid type for argument "+i+" in function "+funcName);
			}
		}
	}

	protected void assertArgumentLength(Object[] arguments, int length) {
		if (arguments.length!=length) {
			throw new IllegalArgumentException(
				"Invalid argument length in function "+funcName);
		}
	}

	protected void assertMinimumArgumentLength(Object[] arguments, int length) {
		if (arguments.length<length) {
			throw new IllegalArgumentException(
				"Must have at least "+length+" arguments in function "+funcName);
		}
	}

	protected void assertMaximumArgumentLength(Object[] arguments, int length) {
		if (arguments.length>length) {
			throw new IllegalArgumentException(
				"Must have no more than "+length+" arguments in function "+funcName);
		}
	}

	protected void assertCorrectNumberOfArguments(Object[] args) {
		if (maxArgs!=-1) {
			assertMaximumArgumentLength(args, maxArgs);
		}
		if (minArgs!=-1) {
			assertMinimumArgumentLength(args, minArgs);
		}
		if (exactArgs!=-1) {
			assertArgumentLength(args, exactArgs);
		}
		if (types.length==1) {
			assertArgumentTypes(args, types[0]);
		}
		if (types.length>1) {
			assertArgumentTypes(args, types);
		}
	}

	/**
	 * @return the initialized
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * @return the strictInitialization
	 */
	protected boolean isStrictInitialization() {
		return strictInitialization;
	}

	/**
	 * @param strictInitialization the strictInitialization to set
	 */
	protected void setStrictInitialization(boolean strictInitialization) {
		this.strictInitialization = strictInitialization;
	}

	/**
	 * @return the functionName
	 */
	protected String getFunctionName() {
		return funcName;
	}

	/**
	 * @param functionName the functionName to set
	 */
	protected void setFunctionName(String funcName) {
		assertNotInitialized();
		this.funcName = funcName;
	}

	/**
	 * @return the exactArgs
	 */
	protected int getExactArgs() {
		return exactArgs;
	}

	/**
	 * @param exactArgs the exactArgs to set
	 */
	protected void setExactArgs(int exactArgs) {
		assertNotInitialized();
		this.exactArgs = exactArgs;
	}

	/**
	 * @return the maxArgs
	 */
	protected int getMaxArgs() {
		return maxArgs;
	}

	/**
	 * @param maxArgs the maxArgs to set
	 */
	protected void setMaxArgs(int maxArgs) {
		assertNotInitialized();
		this.maxArgs = maxArgs;
	}

	/**
	 * @return the minArgs
	 */
	protected int getMinArgs() {
		return minArgs;
	}

	/**
	 * @param minArgs the minArgs to set
	 */
	protected void setMinArgs(int minArgs) {
		assertNotInitialized();
		this.minArgs = minArgs;
	}

	/**
	 * @return the types
	 */
	protected Class<?>[] getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	protected void setTypes(Class<?>... types) {
		assertNotInitialized();
		this.types = types;
	}

}