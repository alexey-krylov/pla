package com.pla.core.hcp.application.service;

/**
 * Created by Mohan Sharma on 12/22/2015.
 */
public class ExcelParserException extends RuntimeException {

    public ExcelParserException(String message){
        super(message);
    }

    public static void raiseDataNotSharedException(){
        throw  new ExcelParserException("Data not found");
    }

    public static void raiseNotValidHeaderException() {
        throw new  ExcelParserException("Header is not valid.");
    }

    public static void raiseFileIsBlank() {
        throw new ExcelParserException("");
    }
}
