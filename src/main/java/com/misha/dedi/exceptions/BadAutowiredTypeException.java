package com.misha.dedi.exceptions;

@SuppressWarnings("serial")
public class BadAutowiredTypeException extends DediException {

    public BadAutowiredTypeException(Class<?> type) {
        super("Failed to autowire type " + type + " because it is either " +
            "abstract, an interface, a primitive, an array type, or void.");
    }
}
