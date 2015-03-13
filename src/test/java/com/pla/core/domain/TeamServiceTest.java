/*
 * Copyright (c) 3/10/15 9:21 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain;

import com.pla.core.application.CreateBenefitCommand;
import com.pla.core.application.CreateTeamCommand;;
import com.pla.core.domain.model.*;
import com.pla.core.domain.service.AdminRoleAdapter;
import com.pla.core.domain.service.BenefitService;
import com.pla.core.domain.service.TeamService;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.core.specification.TeamNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private TeamNameIsUnique teamNameIsUnique;

    @Mock
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Mock
    private IIdGenerator idGenerator;

    private TeamService teamService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        teamService = new TeamService(adminRoleAdapter, teamNameIsUnique, jpaRepositoryFactory, idGenerator);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
       // admin = new Admin("");
    }


    @Test
    public void givenACreateTeamCommandShouldCreateTeamTL() {
        String teamId = "1234";
        CreateTeamCommand createTeamCommand = new CreateTeamCommand();
        createTeamCommand.setUserDetails(userDetails);
        createTeamCommand.setTeamName("CI Team");
        createTeamCommand.setTeamCode("CI TeamCode");
        createTeamCommand.setEmployeeId("1234");

        /*CreateTeamCommand createTeamCommand2 = new CreateTeamCommand();
        createTeamCommand2.setUserDetails(userDetails);
        createTeamCommand2.setTeamName("CI Team");
        createTeamCommand2.setTeamCode("CI TeamCode");
        createTeamCommand2.setEmployeeId("1234");
        createTeamCommand2.setFirstName("Nischitha");
        createTeamCommand2.setLastName("Ramanna");*/

        TeamName teamName = new TeamName(createTeamCommand.getTeamName());
       // TeamCode teamCode = new Team(createTeamCommand.getTeamCode());
       // TeamName teamCode = new TeamName(createTeamCommand.getTeamCode());
        when(idGenerator.nextId()).thenReturn(teamId);
        when(adminRoleAdapter.userToAdmin(createTeamCommand.getUserDetails())).thenReturn(admin);
        when(teamNameIsUnique.isSatisfiedBy(teamName)).thenReturn(true);/*
        teamHandler = new TeamHandler(jpaRepositoryFactory, teamService);
        teamHandler.createTeamHandler(createTeamCommand);*/
       // Team team = teamService.createTeam(createTeamCommand);
       // TeamName createdTeamName = (TeamName) invokeGetterMethod(team, "getTeamName");
       // assertEquals(teamId, invokeGetterMethod(team, "getTeamId"));
       // TeamLeader teamLeader = new TeamLeader("1234",null,null,"Nischitha","Ramanna");
       // assertEquals(teamLeader, invokeGetterMethod(team, "getCurrentTeamLeader"));
      //  assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
       // assertEquals(createTeamCommand.getTeamName(), createTeamCommand.getTeamName());
    }
}
