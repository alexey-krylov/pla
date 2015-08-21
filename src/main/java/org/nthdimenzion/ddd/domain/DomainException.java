/*
 * Copyright (c) 3/9/15 10:59 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.nthdimenzion.ddd.domain;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
