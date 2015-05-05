package com.pla.core.domain.exception;

/**
 * Created by pradyumna on 21-04-2015.
 */
public class DuplicatePlanException extends Exception {

    public DuplicatePlanException(String message) {
        super(message);
    }

    public DuplicatePlanException(String message, Throwable cause) {
        super(message, cause);
    }
}
