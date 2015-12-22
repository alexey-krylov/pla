package com.pla.grouplife.claim.exception;

import org.nthdimenzion.ddd.domain.DomainException;

/**
 * Created by ak
 */
public class GLClaimException extends DomainException {

    public GLClaimException(String message){
        super(message);
    }

    public static void raiseClaimNotFoundException()
    {
        throw  new GLClaimException("ClaimRecord is not found");
    }

    public static void raiseDuplicateClaim()
    {
        throw  new GLClaimException("Claim record is already exist");
    }
    public static void raiseClaimRoutingNotConfiguredForPlan(){
        throw  new GLClaimException("Claim routing is not configured for this plan");
    }
}
