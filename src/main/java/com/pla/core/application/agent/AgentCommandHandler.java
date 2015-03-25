/*
 * Copyright (c) 3/16/15 8:06 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.pla.core.application.exception.AgentApplicationException;
import com.pla.core.domain.service.AgentService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Component
public class AgentCommandHandler {

    private AgentService agentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentCommandHandler.class);

    @Autowired
    public AgentCommandHandler(AgentService agentService) {
        this.agentService = agentService;
    }


    @CommandHandler
    public void createAgentCommandHandler(CreateAgentCommand createAgentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Create Agent Command Received*****" + createAgentCommand);
        }
        try {
            agentService.createAgent(createAgentCommand);
        } catch (Exception e) {
            LOGGER.error("*****Saving agent failed*****", e);
            throw new AgentApplicationException(e.getMessage());
        }

    }

    @CommandHandler
    public void updateAgentCommandHandler(UpdateAgentCommand updateAgentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Update Agent Command Received*****" + updateAgentCommand);
        }
        try {
            agentService.updateAgent(updateAgentCommand);
        } catch (Exception e) {
            LOGGER.error("*****Update agent failed*****", e);
            throw new AgentApplicationException(e.getMessage());
        }

    }
}
