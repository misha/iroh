package com.github.msoliter.iroh.container.annotations;

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
    
    /* This component's qualifier, if its supertype has many implementations. */
    public String qualifier() default "";

    /* The scope of the component - 'singleton' or 'prototype'. */
    public String scope() default "singleton";
    
    /* Whether or not this component should override an existing definition of
     * a component with the same type. */
    public boolean override() default false;
}
