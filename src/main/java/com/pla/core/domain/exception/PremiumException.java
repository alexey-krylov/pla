package com.pla.core.domain.exception;

import org.nthdimenzion.ddd.domain.DomainException;

/**
 * Created by Samir on 4/13/2015.
 */
public class PremiumException extends DomainException {


    private PremiumException(String message) {
        super(message);
    }

    public static void raisePremiumNotFoundException() {
        throw new PremiumException("Premium not found");
    }

    public static void raiseInfluencingFactorMismatchException() {
        throw new PremiumException("Provided influencing factor combination does not match with premium setup influencing factors");
    }
}
