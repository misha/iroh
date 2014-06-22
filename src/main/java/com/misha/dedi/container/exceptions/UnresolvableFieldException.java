package com.misha.dedi.container.exceptions;

import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class UnresolvableFieldException extends DediException {

    public UnresolvableFieldException(Field field) {
        super("The field '" + field.getName() + "' in type " + 
            field.getType() + " does not have a resolvable component type.");
    }
}
