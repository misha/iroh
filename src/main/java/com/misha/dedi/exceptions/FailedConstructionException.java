package com.misha.dedi.exceptions;


@SuppressWarnings("serial")
public class FailedConstructionException extends DediException {

    public FailedConstructionException(Throwable cause) {
        super(cause);
    }
}
