/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.application.CreateTeamCommand;
import com.pla.core.application.UpdateBenefitCommand;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.Team;
import com.pla.core.specification.TeamNameIsUnique;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.AbstractDomainFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@DomainFactory
public class TeamService extends AbstractDomainFactory {


    private AdminRoleAdapter adminRoleAdapter;

    private TeamNameIsUnique teamNameIsUnique;

    private JpaRepositoryFactory jpaRepositoryFactory;

    private IIdGenerator idGenerator;

    private Logger logger = LoggerFactory.getLogger(AdminRoleAdapter.class);

    @Autowired
    public TeamService(AdminRoleAdapter adminRoleAdapter, TeamNameIsUnique teamNameIsUnique, JpaRepositoryFactory jpaRepositoryFactory, IIdGenerator idGenerator){
        this.adminRoleAdapter = adminRoleAdapter;
        this.teamNameIsUnique = teamNameIsUnique;
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.idGenerator = idGenerator;
    }

    public Team createTeam(CreateTeamCommand createTeamCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("Benefit command received" + createTeamCommand);
        }
        String teamId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(createTeamCommand.getUserDetails());
        Team team = admin.createTeam(teamNameIsUnique, teamId, createTeamCommand.getTeamName(), createTeamCommand.getTeamCode(),
                createTeamCommand.getEmployeeId(), createTeamCommand.getFromDate(), createTeamCommand.getThruDate(), createTeamCommand.getFirstName(), createTeamCommand.getLastName());
        return team;
    }

    /*public Benefit updateTeam(UpdateBenefitCommand updateBenefitCommand) {
        String benefitId = updateBenefitCommand.getBenefitId();
        Admin admin = adminRoleAdapter.userToAdmin(updateBenefitCommand.getUserDetails());
        Benefit benefit = getBenefit(benefitId);
        benefit = admin.updateBenefit(benefit, updateBenefitCommand.getBenefitName());
        return benefit;

    }*/
}
