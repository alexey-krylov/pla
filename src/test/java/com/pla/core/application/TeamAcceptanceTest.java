/*
 * Copyright (c) 3/5/15 6:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class TeamAcceptanceTest {

    private Logger logger = LoggerFactory.getLogger(TeamAcceptanceTest.class);

    private UserDetails userDetails;

    @Autowired
    private CommandGateway commandGateway;


    @Before
    public void setUp() {
        UserLoginDetailDto userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
        List<String> permissions = Lists.newArrayList();
        permissions.add("ROLE_ADMIN");
        userDetails = userLoginDetailDto.populateAuthorities(permissions);
    }

    @Test
    @ExpectedDatabase(value = "classpath:testdata/endtoend/team/expectedteamdata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenTeamNameAndTeamLeadItShouldCreateTeamWithTeamLead() {
        CreateTeamCommand createTeamCommand = new CreateTeamCommand();
        createTeamCommand.setUserDetails(userDetails);
        createTeamCommand.setTeamName("CI Team 2");
        createTeamCommand.setTeamCode("CI TeamCode 2");
        createTeamCommand.setEmployeeId("1234");
        createTeamCommand.setFirstName("CI TL");
        createTeamCommand.setLastName("CI TL LN");
        createTeamCommand.setFromDate(LocalDate.now());
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(createTeamCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating team", e);
        }
        assertTrue(isSuccess);

    }
    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/team/testdataforupdateteam.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/team/expectedupdateteamdata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenTeamLeadItShouldUpdateTeamWithTeamLead() {
        UpdateTeamCommand updateTeamCommand = new UpdateTeamCommand();
        updateTeamCommand.setEmployeeId("3456");
        updateTeamCommand.setFirstName("CI TL2");
        updateTeamCommand.setLastName("CI TL LN2");
        updateTeamCommand.setFromDate(LocalDate.now());
        updateTeamCommand.setTeamId("1");
        updateTeamCommand.setUserDetails(userDetails);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(updateTeamCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating team", e);
        }
        assertTrue(isSuccess);

    }
}
