package com.misha.dedi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a string qualifier to distinguish a class from other beans of
 * the identical type, or to distinguish between two subclasses when injecting
 * for a supertype.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {

    public String value();
}
