package com.github.msoliter.iroh.container.exceptions;

@SuppressWarnings("serial")
public class BadAutowiredTypeException extends RuntimeException {

    public BadAutowiredTypeException(Class<?> type) {
        super("Failed to autowire type " + type + " because it is either " +
            "abstract, an interface, a primitive, an array type, or void.");
    }
}
