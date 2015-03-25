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
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.domain.model.agent.LicenseNumber;
import com.pla.core.specification.AgentLicenseNumberIsUnique;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@DomainService
public class AgentService {

    private AdminRoleAdapter adminRoleAdapter;

    private AgentLicenseNumberIsUnique agentLicenseNumberIsUnique;

    private JpaRepositoryFactory jpaRepositoryFactory;

    @Autowired
    public AgentService(AdminRoleAdapter adminRoleAdapter, AgentLicenseNumberIsUnique agentLicenseNumberIsUnique, JpaRepositoryFactory jpaRepositoryFactory) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.agentLicenseNumberIsUnique = agentLicenseNumberIsUnique;
        this.jpaRepositoryFactory = jpaRepositoryFactory;
    }

    public void createAgent(CreateAgentCommand createAgentCommand) {
        UtilValidator.isNotEmpty("");
        boolean isLicenseNumberUnique = UtilValidator.isNotEmpty(createAgentCommand.getLicenseNumber().getLicenseNumber()) ? agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(createAgentCommand.getLicenseNumber().getLicenseNumber())) : true;
        Admin admin = adminRoleAdapter.userToAdmin(createAgentCommand.getUserDetails());
        Agent agent = admin.createAgent(isLicenseNumberUnique, createAgentCommand);
        JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
        agentRepository.save(agent);
    }

    public void updateAgent(UpdateAgentCommand updateAgentCommand) {
        boolean isLicenseNumberUnique = true;
        JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
        Agent agent = agentRepository.getOne(new AgentId(updateAgentCommand.getAgentId()));
        if (UtilValidator.isNotEmpty(updateAgentCommand.getLicenseNumber().getLicenseNumber()) && !(agent.getLicenseNumber().getLicenseNumber().equals(updateAgentCommand.getLicenseNumber().getLicenseNumber()))) {
            isLicenseNumberUnique = agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(updateAgentCommand.getLicenseNumber().getLicenseNumber()));
        }
        Admin admin = adminRoleAdapter.userToAdmin(updateAgentCommand.getUserDetails());
        Agent updatedAgent = admin.updateAgent(agent, isLicenseNumberUnique, updateAgentCommand);
        agentRepository.save(updatedAgent);
    }
}
