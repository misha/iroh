package com.github.msoliter.iroh.container.exceptions.base;

/**
 * Represents the base class for all Iroh exceptions.
 */
public class IrohException extends RuntimeException {
    
    /* This exception's serial number. */
    private static final long serialVersionUID = -4591906383960858790L;

    /**
     * Builds a non-specific exception caused by an exception unrelated to
     * Iroh's internal workings.
     * 
     * @param cause The cause of this exception.
     */
    public IrohException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Used by extensions of this class to provide access to the exception
     * class' message field. Builds a specific Iroh-based exception.
     * 
     * @param message The message to use for this exception.
     */
    protected IrohException(String message) {
        super(message);
    }
    
    /**
     * Used by extensions of this class to provide access to the exception
     * class' cause and message fields. Builds a specific Iroh-based exception.
     * 
     * @param message The message to use for this exception.
     * @param cause The exception that caused this one to occur.
     */
    protected IrohException(String message, Throwable cause) {
        super(message, cause);
    }
}
