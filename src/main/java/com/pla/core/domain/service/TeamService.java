/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Team;
import com.pla.core.dto.TeamDto;
import com.pla.core.specification.TeamAssociatedWithAgent;
import com.pla.core.specification.TeamIsUnique;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@DomainService
public class TeamService {


    private AdminRoleAdapter adminRoleAdapter;

    private TeamIsUnique teamIsUnique;

    private TeamAssociatedWithAgent teamAssociatedWithAgent;

    private IIdGenerator idGenerator;

    @Autowired
    public TeamService(AdminRoleAdapter adminRoleAdapter, TeamIsUnique teamIsUnique, IIdGenerator idGenerator, TeamAssociatedWithAgent teamAssociatedWithAgent) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.teamIsUnique = teamIsUnique;
        this.idGenerator = idGenerator;
        this.teamAssociatedWithAgent = teamAssociatedWithAgent;
    }

    public Team createTeam(String teamName, String teamCode, String regionCode, String branchCode, String employeeId, LocalDate fromDate, String firstName, String lastName, UserDetails userDetails) {
        String teamId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        TeamDto teamDto = new TeamDto(teamName, teamCode, teamId);
        boolean isTeamUnique = teamIsUnique.isSatisfiedBy(teamDto);
        Team team = admin.createTeam(isTeamUnique, teamId, teamName, teamCode, regionCode, branchCode, employeeId, fromDate, firstName, lastName);
        return team;
    }

    public Team updateTeamLead(Team team, String employeeId, String firstName, String lastName, LocalDate fromDate, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        team = admin.updateTeamLead(team, employeeId, firstName, lastName, fromDate);
        return team;
    }

    public Team inactivateTeam(Team team, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        TeamDto teamDto = new TeamDto(team.getTeamName(), team.getTeamCode(), team.getTeamId());
        boolean isTeamAssociatedWithAgent = teamAssociatedWithAgent.isSatisfiedBy(teamDto);
        return admin.inactivateTeam(team, isTeamAssociatedWithAgent);
    }

}
