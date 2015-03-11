/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.domain.model.Team;
import com.pla.core.domain.service.TeamService;
import com.pla.core.domain.service.TeamService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Component
public class TeamHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private TeamService teamService;

    private Logger logger = LoggerFactory.getLogger(TeamHandler.class);

    @Autowired
    public TeamHandler(JpaRepositoryFactory jpaRepositoryFactory, TeamService teamService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.teamService = teamService;
    }

    @CommandHandler
    public void createTeamHandler(CreateTeamCommand createTeamCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Command Received*****" + createTeamCommand);
        }
        Team team = teamService.createTeam(createTeamCommand);
        CrudRepository<Team, String> teamRepository = jpaRepositoryFactory.getCrudRepository(Team.class);
        try {
            teamRepository.save(team);
        } catch (RuntimeException e) {
            logger.error("*****Saving benefit failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
