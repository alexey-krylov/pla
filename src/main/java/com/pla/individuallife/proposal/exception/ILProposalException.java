package com.pla.individuallife.proposal.exception;

/**
 * Created by Admin on 8/10/2015.
 */
public class ILProposalException extends RuntimeException {

    private ILProposalException(String message) {
        super(message);
    }


    public static void raiseMandatoryDocumentNotUploaded() {
        throw new ILProposalException("Mandatory Documents does not submitted by the processor");
    }
}