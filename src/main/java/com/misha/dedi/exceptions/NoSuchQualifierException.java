package com.misha.dedi.exceptions;

/**
 * Thrown when an unprocessed qualifier is used in an autowired annotation.
 */
@SuppressWarnings("serial")
public class NoSuchQualifierException extends Exception {

    public NoSuchQualifierException(String qualifier) {
        super("The qualifier '" + qualifier + "' was not found");
    }
}
