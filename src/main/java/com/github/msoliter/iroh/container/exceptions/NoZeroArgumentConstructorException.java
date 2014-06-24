package com.github.msoliter.iroh.container.exceptions;

/**
 * Represents an exception thrown when an autowired type did not have a suitable
 * constructor. In general, that means the no-argument constructor was missing
 * or otherwise blocked (in the case of a hard-coded constructor or an enum).
 */
@SuppressWarnings("serial")
public class NoZeroArgumentConstructorException extends RuntimeException {

    public NoZeroArgumentConstructorException(Class<?> type) {
        super("No zero argument constructor was found for type " + type);
    }
}
