package com.misha.dedi.container.exceptions;

/**
 * Represents an exception thrown when a base type is autowired, but there
 * appears to be more than one concrete implementation of that base type
 * available for autowiring, and no qualifier exists to resolve it.
 */
@SuppressWarnings("serial")
public class UnexpectedImplementationCountException extends DediException {

    public UnexpectedImplementationCountException(Class<?> type, int count) {
        super("Expected exactly 1 implementation of type " + type + " but found " + count);
    }
}
