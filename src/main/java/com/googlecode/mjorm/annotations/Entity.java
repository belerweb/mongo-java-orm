package com.googlecode.mjorm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.mjorm.DiscriminatorType;

/**
 * Marks a class as an entity and able to be
 * mapped to MongoDB objects.
 * 
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Entity {

	/**
	 * The discriminator name
	 * @return
	 */
	String discriminatorName() default "";

	/**
	 * The discriminator type
	 * @return
	 */
	DiscriminatorType discriminatorType() default DiscriminatorType.STRING;

	/**
	 * This subclasses.
	 * @return
	 */
	SubClass[] subClasses() default {};

}
