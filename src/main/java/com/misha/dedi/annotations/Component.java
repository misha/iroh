package com.misha.dedi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * States that the annotated type should be injectable via its zero argument
 * constructor, or that the annotated method produces an object that should
 * be injectable by calling that method with no parameters.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    /* The scope of the component - 'singleton' or 'prototype'. */
    public String scope() default "singleton";
    
    /* This component's qualifier, if its supertype has many implementations. */
    public String qualifier() default "";
}
