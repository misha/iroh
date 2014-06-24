package com.github.msoliter.iroh.container.exceptions;

@SuppressWarnings("serial")
public class FailedConstructionException extends RuntimeException {

    public FailedConstructionException(Throwable cause) {
        super(cause);
    }
}
