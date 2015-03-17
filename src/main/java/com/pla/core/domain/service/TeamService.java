/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Team;
import com.pla.core.domain.model.TeamCode;
import com.pla.core.domain.model.TeamName;
import com.pla.core.specification.TeamCodeIsUnique;
import com.pla.core.specification.TeamNameIsUnique;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@DomainService
public class TeamService {


    private AdminRoleAdapter adminRoleAdapter;

    private TeamNameIsUnique teamNameIsUnique;

    private TeamCodeIsUnique teamCodeIsUnique;

    private IIdGenerator idGenerator;

    @Autowired
    public TeamService(AdminRoleAdapter adminRoleAdapter, TeamNameIsUnique teamNameIsUnique, IIdGenerator idGenerator){
        this.adminRoleAdapter = adminRoleAdapter;
        this.teamNameIsUnique = teamNameIsUnique;
        this.idGenerator = idGenerator;
    }

    public Team createTeam(String teamName, String teamCode,String employeeId, LocalDate fromDate, String firstName, String lastName, UserDetails userDetails) {
        String teamId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        TeamName teamName1 = new TeamName(teamName);
        TeamCode teamCode1 =  new TeamCode(teamCode);
        boolean isTeamNameUnique = teamNameIsUnique.isSatisfiedBy(teamName1);
        boolean isTeamCodeUnique = teamCodeIsUnique.isSatisfiedBy(teamCode1);
        Team team = admin.createTeam(isTeamNameUnique, isTeamCodeUnique, teamId, teamName, teamCode, employeeId, fromDate, firstName, lastName);
        return team;
    }

    public Team updateTeamLead(Team team, String employeeId, String firstName, String lastName, LocalDate fromDate, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        team = admin.updateTeamLead(team, employeeId, firstName, lastName, fromDate);
        return team;
    }

}
