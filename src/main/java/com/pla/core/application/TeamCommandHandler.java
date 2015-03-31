/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.domain.model.Team;
import com.pla.core.domain.service.TeamService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Component
public class TeamCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private TeamService teamService;

    private Logger logger = LoggerFactory.getLogger(TeamCommandHandler.class);

    @Autowired
    public TeamCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, TeamService teamService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.teamService = teamService;
    }

    @CommandHandler
    public void createTeamHandler(CreateTeamCommand createTeamCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Create Team Command Received*****" + createTeamCommand);
        }
        Team team = teamService.createTeam(createTeamCommand.getTeamName(), createTeamCommand.getTeamCode(), createTeamCommand.getRegionCode(), createTeamCommand.getBranchCode()
                , createTeamCommand.getEmployeeId(), createTeamCommand.getFromDate(), createTeamCommand.getFirstName(), createTeamCommand.getLastName(), createTeamCommand.getUserDetails());
        JpaRepository<Team, String> teamRepository = jpaRepositoryFactory.getCrudRepository(Team.class);
        try {
            teamRepository.save(team);
        } catch (RuntimeException e) {
            logger.error("*****Saving Team failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateTeamHandler(UpdateTeamCommand updateTeamCommand) {

        if (logger.isDebugEnabled()) {
            logger.debug("*****Command Received*****" + updateTeamCommand);
        }
        JpaRepository<Team, String> teamRepository = jpaRepositoryFactory.getCrudRepository(Team.class);
        Team team = teamRepository.findOne(updateTeamCommand.getTeamId());
        team = teamService.updateTeamLead(team, updateTeamCommand.getEmployeeId(), updateTeamCommand.getFirstName(), updateTeamCommand.getLastName(), updateTeamCommand.getFromDate(),
                updateTeamCommand.getUserDetails());
        try {
            teamRepository.save(team);
        } catch (RuntimeException e) {
            logger.error("*****Updating Team failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @CommandHandler
    public void inactivateTeamHandler(InactivateTeamCommand inactivateTeamCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Inactivate team Status Command  Received*****" + inactivateTeamCommand);
        }
        JpaRepository<Team, String> teamRepository = jpaRepositoryFactory.getCrudRepository(Team.class);
        Team team = teamRepository.findOne(inactivateTeamCommand.getTeamId());
        team = teamService.inactivateTeam(team, inactivateTeamCommand.getUserDetails());
        try {
            teamRepository.save(team);
        } catch (RuntimeException e) {
            logger.error("*****Updating Team failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
