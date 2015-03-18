/*
 * Copyright (c) 3/16/15 8:06 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.pla.core.application.exception.AgentApplicationException;
import com.pla.core.domain.model.agent.Agent;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.domain.service.AgentService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Component
public class AgentCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private AgentService agentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentCommandHandler.class);

    @Autowired
    public AgentCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, AgentService agentService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.agentService = agentService;
    }


    @CommandHandler
    public void createAgentCommandHandler(CreateAgentCommand createAgentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Create Agent Command Received*****" + createAgentCommand);
        }
        try {
            Agent agent = agentService.createAgent(createAgentCommand);
            JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
            agentRepository.save(agent);
        } catch (Exception e) {
            LOGGER.error("*****Saving agent failed*****", e);
            throw new AgentApplicationException(e.getMessage());
        }

    }
}
