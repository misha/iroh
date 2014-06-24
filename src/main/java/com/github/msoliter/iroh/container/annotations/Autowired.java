package com.github.msoliter.iroh.container.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells dedi that the annotated field should be injected with an instance.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    /* A qualifier value for disambiguating a field in the case of multiple
     * available implementations. */
    public String qualifier() default "";
    
    /* Whether or not this autowiring should be done lazily or not. */
    public boolean lazy() default false;
}
