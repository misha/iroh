package com.misha.dedi.exceptions;

@SuppressWarnings("serial")
public abstract class DediException extends Exception {

    public DediException(String message) {
        super(message);
    }
    
    public DediException(Throwable cause) {
        super(cause);
    }
}
