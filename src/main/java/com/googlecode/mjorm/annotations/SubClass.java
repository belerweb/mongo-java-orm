package com.googlecode.mjorm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a sublcass of an entity.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubClass {

	/**
	 * The entity subclass.
	 * @return
	 */
	Class<?> entityClass();

	/**
	 * The discriminator value.
	 * @return
	 */
	String discriminiatorValue();

}
