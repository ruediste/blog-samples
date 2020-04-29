package com.github.ruediste.blogSamples.gsonPolymorphism;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines the name used for a class in the serialized json. If absent the
 * {@link Class#getSimpleName() simple class name} us used, converted to lower
 * camel.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface GsonPolymorphName {
	String value();
}
