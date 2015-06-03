package com.pla.sharedkernel.exception;

/**
 * Created by Admin on 6/3/2015.
 */
public class ProcessInfoException extends Exception {

    public ProcessInfoException(String message) {
        super(message);
    }


    public static void raiseProcessTypeNotFoundException(String process) throws ProcessInfoException {
        throw new ProcessInfoException("The "+process+" process is not configured");
    }

    public static void raiseProductLineItemNotFoundException() throws ProcessInfoException {
        throw new ProcessInfoException("Product Line Item not found");
    }
}
