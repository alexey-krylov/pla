/*
 * Copyright (c) 3/9/15 11:01 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.exception;

import org.nthdimenzion.ddd.domain.DomainException;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
public class BenefitException extends DomainException {


    private BenefitException(String message) {
        super(message);
    }


    public static void raiseBenefitNameNotProperLengthException() {
        throw new BenefitException("Benefit name cannot be empty or more than 100 characters");

    }

    public static void raiseBenefitNameNotUniqueException() {
        throw new BenefitException("Benefit name is not unique");

    }
}
