package com.misha.dedi.container.exceptions;

@SuppressWarnings("serial")
public class FailedConstructionException extends DediException {

    public FailedConstructionException(Throwable cause) {
        super(cause);
    }
}