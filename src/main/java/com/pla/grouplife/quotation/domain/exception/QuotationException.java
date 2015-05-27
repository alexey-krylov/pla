package com.pla.grouplife.quotation.domain.exception;

/**
 * Created by Samir on 4/7/2015.
 */
public class QuotationException extends RuntimeException {

    private QuotationException(String message) {
        super(message);
    }

    public static void raiseQuotationNotModifiableException() {
        throw new QuotationException("Quotation Cannot be modified");
    }
}
