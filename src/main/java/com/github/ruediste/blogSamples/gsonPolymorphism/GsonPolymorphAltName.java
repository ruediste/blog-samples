package com.github.ruediste.blogSamples.gsonPolymorphism;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies additional names for a subclass, used during serialization.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface GsonPolymorphAltName {
	String[] value();
}
