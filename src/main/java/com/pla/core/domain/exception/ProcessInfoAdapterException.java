package com.pla.core.domain.exception;

/**
 * Created by Admin on 6/3/2015.
 */
public class ProcessInfoAdapterException extends Exception {

    public ProcessInfoAdapterException(String message) {
        super(message);
    }


    public static void raiseProcessLineItemNotFoundException(String process) throws ProcessInfoAdapterException {
        throw new ProcessInfoAdapterException("Process Line Item value not found for "+process+" process ");
    }
}
