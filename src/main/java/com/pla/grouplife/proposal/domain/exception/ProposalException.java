package com.pla.grouplife.proposal.domain.exception;

/**
 * Created by Samir on 4/7/2015.
 */
public class ProposalException extends RuntimeException {

    private ProposalException(String message) {
        super(message);
    }

    public static void raiseQuotationNotModifiableException() {
        throw new ProposalException("Quotation Cannot be modified");
    }

    public static void raiseAgentIsInactiveException() {
        throw new ProposalException("Quotation cannot be updated as agent is inactive.");
    }
}
