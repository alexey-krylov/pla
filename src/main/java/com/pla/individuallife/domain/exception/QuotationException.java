package com.pla.individuallife.domain.exception;

/**
 * Created by Karunakar on 5/19/2015.
 */
public class QuotationException extends RuntimeException {

    private QuotationException(String message) {
        super(message);
    }

    public static void raiseQuotationNotModifiableException() {
        throw new QuotationException("Quotation Cannot be modified");
    }
}
