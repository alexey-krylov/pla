/*
 * Copyright (c) 3/10/15 9:21 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.application.CreateTeamCommand;
import com.pla.core.domain.model.Admin;
import com.pla.core.dto.TeamDto;
import com.pla.core.specification.TeamAssociatedWithAgent;
import com.pla.core.specification.TeamIsUnique;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.when;

/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private TeamIsUnique teamIsUnique;

    @Mock
    TeamAssociatedWithAgent teamAssociatedWithAgent;

    @Mock
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Mock
    private IIdGenerator idGenerator;

    private TeamService teamService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        teamService = new TeamService(adminRoleAdapter, teamIsUnique, idGenerator, teamAssociatedWithAgent);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
    }


    @Test
    public void givenACreateTeamCommandShouldCreateTeamTL() {
        String teamId = "1234";
        CreateTeamCommand createTeamCommand = new CreateTeamCommand();
        createTeamCommand.setUserDetails(userDetails);
        createTeamCommand.setTeamName("CI Team");
        createTeamCommand.setTeamCode("CI TeamCode");
        createTeamCommand.setEmployeeId("1234");
        TeamDto teamName = new TeamDto(createTeamCommand.getTeamName(), createTeamCommand.getTeamCode(), teamId);
        when(idGenerator.nextId()).thenReturn(teamId);
        when(adminRoleAdapter.userToAdmin(createTeamCommand.getUserDetails())).thenReturn(admin);
        when(teamIsUnique.isSatisfiedBy(teamName)).thenReturn(true);
    }
}