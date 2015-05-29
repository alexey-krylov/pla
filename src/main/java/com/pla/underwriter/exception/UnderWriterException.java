package com.pla.underwriter.exception;

/**
 * Created by Admin on 5/27/2015.
 */
public class UnderWriterException extends RuntimeException {

    private UnderWriterException(String message) {
        super(message);
    }

    public static void raiseInfluencingFactorMismatchException(){
        throw new UnderWriterException("Provided influencing factor combination does not match with UnderWriter setup influencing factors");
    }

    public static void raiseUnderWriterNotFoundException(){
        throw  new UnderWriterException("UnderWriter is not found");
    }

}
