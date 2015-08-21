package com.pla.grouphealth.proposal.domain.exception;

/**
 * Created by Samir on 7/7/2015.
 */
public class GHProposalException extends RuntimeException{


    private GHProposalException(String message) {
        super(message);
    }

    public static void raiseAgentIsInactiveException() {
        throw new GHProposalException("Proposal cannot be updated as agent is inactive.");
    }
}
