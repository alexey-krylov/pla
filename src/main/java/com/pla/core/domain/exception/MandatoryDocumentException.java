package com.pla.core.domain.exception;

import org.nthdimenzion.ddd.domain.DomainException;

/**
 * Created by Admin on 3/27/2015.
 */
public class MandatoryDocumentException extends DomainException {

    public MandatoryDocumentException(String message) {
        super(message);
    }
}
