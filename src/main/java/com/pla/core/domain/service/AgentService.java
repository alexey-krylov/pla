/*
 * Copyright (c) 3/16/15 8:10 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.application.agent.CreateAgentCommand;
import com.pla.core.application.agent.UpdateAgentCommand;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.agent.Agent;
import com.pla.core.domain.model.agent.LicenseNumber;
import com.pla.core.specification.AgentLicenseNumberIsUnique;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@DomainService
public class AgentService {

    private AdminRoleAdapter adminRoleAdapter;

    private AgentLicenseNumberIsUnique agentLicenseNumberIsUnique;

    @Autowired
    public AgentService(AdminRoleAdapter adminRoleAdapter, AgentLicenseNumberIsUnique agentLicenseNumberIsUnique) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.agentLicenseNumberIsUnique = agentLicenseNumberIsUnique;
    }

    public Agent createAgent(CreateAgentCommand createAgentCommand) {
        boolean isLicenseNumberUnique = agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(createAgentCommand.getLicenseNumber().getLicenseNumber()));
        Admin admin = adminRoleAdapter.userToAdmin(createAgentCommand.getUserDetails());
        return admin.createAgent(isLicenseNumberUnique, createAgentCommand);
    }

    public Agent updateAgent(Agent agent, UpdateAgentCommand createAgentCommand) {
        boolean isLicenseNumberUnique = agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(createAgentCommand.getLicenseNumber().getLicenseNumber()));
        Admin admin = adminRoleAdapter.userToAdmin(createAgentCommand.getUserDetails());
        return admin.updateAgent(agent, isLicenseNumberUnique, createAgentCommand);
    }
}
