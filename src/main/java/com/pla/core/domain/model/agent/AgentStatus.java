/*
 * Copyright (c) 3/13/15 8:18 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
public enum AgentStatus {

    ACTIVE("Active"), INACTIVE("InActive"), TERMINATED("Terminated");

    private String description;

    AgentStatus(String description) {
        this.description = description;

    }
}
