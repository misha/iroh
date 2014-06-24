package com.github.msoliter.iroh.container.exceptions;

import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class UnresolvableFieldException extends RuntimeException {

    public UnresolvableFieldException(Field field) {
        super("The field '" + field.getName() + "' in type " + 
            field.getType() + " does not have a resolvable component type.");
    }
}
