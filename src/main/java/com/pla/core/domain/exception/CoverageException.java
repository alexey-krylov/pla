/*
 * Copyright (c) 3/10/15 8:58 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.exception;

import org.nthdimenzion.ddd.domain.DomainException;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
public class CoverageException extends DomainException {

    private CoverageException(String message) {
        super(message);
    }


    public static void raiseCoverageNotUpdatableException() {
        throw new CoverageException("Coverage cannot be uupdated as it is used in active plan");
    }
}
