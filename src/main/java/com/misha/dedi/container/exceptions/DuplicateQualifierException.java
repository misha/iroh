package com.misha.dedi.container.exceptions;

@SuppressWarnings("serial")
public class DuplicateQualifierException extends DediException {

    public DuplicateQualifierException(String qualifier) {
        super("The qualifier '" + qualifier + "' was placed on multiple types.");
    }
}
