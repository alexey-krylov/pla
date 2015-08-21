package com.pla.grouphealth.quotation.domain.exception;

/**
 * Created by Samir on 4/7/2015.
 */
public class GHQuotationException extends RuntimeException {

    private GHQuotationException(String message) {
        super(message);
    }

    public static void raiseQuotationNotModifiableException() {
        throw new GHQuotationException("Quotation Cannot be modified");
    }

    public static void raiseAgentIsInactiveException() {
        throw new GHQuotationException("Quotation cannot be updated as agent is inactive.");
    }
}
