package com.github.msoliter.iroh.container.exceptions;

@SuppressWarnings("serial")
public class DuplicateQualifierException extends RuntimeException {

    public DuplicateQualifierException(String qualifier) {
        super("The qualifier '" + qualifier + "' was placed on multiple types.");
    }
}
