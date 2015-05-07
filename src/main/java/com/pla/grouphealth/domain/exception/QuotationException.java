package com.pla.grouphealth.domain.exception;

/**
 * Created by Karunakar on 4/30/2015.
 */
public class QuotationException extends RuntimeException {

    private QuotationException(String message) {
        super(message);
    }

    public static void raiseQuotationNotModifiableException() {
        throw new QuotationException("Quotation Cannot be modified");
    }
}
