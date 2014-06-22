package com.misha.dedi.exceptions;

@SuppressWarnings("serial")
public class ComponentMethodInPrototypeException extends DediException {

    public ComponentMethodInPrototypeException(Class<?> type) {
        super("The type " + type + " declares component methods, but its " +
            "scope is not 'singleton'.");
    }
}
