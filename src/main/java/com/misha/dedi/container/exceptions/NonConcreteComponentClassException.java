package com.misha.dedi.container.exceptions;

@SuppressWarnings("serial")
public class NonConcreteComponentClassException extends DediException {

    public NonConcreteComponentClassException(Class<?> type) {
        super("The type " + type + " was marked with @Component, but is " +
            "not a concrete type. @Component may not be placed on abstract " +
            "classes or interfaces.");
    }
}
