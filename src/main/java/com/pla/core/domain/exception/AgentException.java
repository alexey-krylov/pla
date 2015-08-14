/*
 * Copyright (c) 3/16/15 5:45 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.exception;

import org.nthdimenzion.ddd.domain.DomainException;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
public class AgentException extends DomainException {

    private AgentException(String message) {
        super(message);
    }

    public static AgentException raiseAgentUpdateNotAllowedException(String message) {
        throw new AgentException(message);
    }

    public static AgentException raiseAgentLicenseNumberUniqueException() {
        throw new AgentException("Agent cannot be updated as license number is in use");
    }

    public static AgentException raiseAgentNrcNumberUniqueException() {
        throw new AgentException("Agent cannot be updated as nrc number is in use");
    }
}
