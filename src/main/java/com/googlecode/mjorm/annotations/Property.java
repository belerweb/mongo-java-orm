package com.googlecode.mjorm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a property that will be mapped
 * to a field on a document.  All of this annotations
 * parameters are optional.  This annotation must
 * be used on property getter methods, methods that
 * start with "{@link is}" or "{@link get}".
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Property {

	/**
	 * Optionally specifies the name of the field
	 * on the MongoDB document that the property
	 * annotated by this will use.  By default it will
	 * take on the annotated property's name.
	 * @return
	 */
	String field() default "";

	/**
	 * Optionally specifies the type that should
	 * be used for this property.  By default the
	 * method's return type is used.
	 * @return
	 */
	Class<?> type() default void.class;

	/**
	 * Optionally specifies the type that should
	 * be used to store this property.  By default the
	 * method's return type is used.
	 * @return
	 */
	Class<?> storageType() default void.class;

	/**
	 * Optionally specifies the type parameters for
	 * any generic type parameters specified by
	 * {@link #type()} (or the type that is inferred).
	 * @return
	 */
	Class<?>[] genericParameterTypes() default {};

	/**
	 * The class that generates values for the property.
	 * @return the value generator
	 */
	Class<?> valueGeneratorClass() default void.class;

	/**
	 * Optionally specifies conversion hints that
	 * are used by the {@link ObjectMapper} when
	 * conversion this property to and from the
	 * MongoDB document's field.
	 * @return
	 */
	TypeConversionHint[] typeConversionHints() default {};

}
