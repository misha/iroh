package com.misha.dedi.exceptions;

import java.lang.reflect.Field;


@SuppressWarnings("serial")
public class DependencyCycleException extends DediException {

    public DependencyCycleException(Field field, Class<?> objectType) {
        super(
            String.format(
                "A dependency cycle was detected when checking the field " +
                "'%s' of type '%s' in the class '%s'.",
                field.getName(),
                field.getType().toString(),
                objectType.toString()));
    }
}
