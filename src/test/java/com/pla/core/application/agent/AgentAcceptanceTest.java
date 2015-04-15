/*
 * Copyright (c) 3/25/15 1:05 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentStatus;
import com.pla.core.dto.*;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class AgentAcceptanceTest {

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
    @ExpectedDatabase(value = "classpath:testdata/endtoend/agent/expectedagentcreateddata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenAgentDetailWithUniqueLicenseNumberItShouldCreateAgent() {
        CreateAgentCommand createAgentCommand = new CreateAgentCommand();
        createAgentCommand.setAgentId("100011");
        createAgentCommand.setAuthorizePlansToSell(Sets.newHashSet(new PlanId("1000"), new PlanId("1001")));
        createAgentCommand.setUserDetails(userDetails);
        createAgentCommand.setAgentStatus(AgentStatus.ACTIVE);
        DesignationDto designationDto = new DesignationDto("Employee", "Employee");
        LocalDate trainingCompletionDate = new LocalDate(2015, 3, 24);
        createAgentCommand.setAgentProfile(new AgentProfileDto("Mr", "Test", "Agent", 1111, "EMP002", trainingCompletionDate, designationDto));
        createAgentCommand.setLicenseNumber(new LicenseNumberDto("LIC0001"));
        GeoDetailDto geoDetailDto = new GeoDetailDto(560068, "Bangalore", "India");
        createAgentCommand.setContactDetail(new ContactDetailDto("9916971271", "9916971271", "9916971271", "abc@def.com", "Kormangala", "Kormangala", geoDetailDto));
        createAgentCommand.setPhysicalAddress(new PhysicalAddressDto("Bomanahalli", "Bomanahalli", geoDetailDto));
        createAgentCommand.setChannelType(new ChannelTypeDto("Broaker", "Broaker"));
        createAgentCommand.setTeamDetail(new TeamDetailDto("TEAM001"));
        commandGateway.sendAndWait(createAgentCommand);
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/agent/existingagentdata.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/agent/expectedagentcreateddata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/agent/existingagentdata.xml", type = DatabaseOperation.TRUNCATE_TABLE)
    public void givenAgentDetailWithUniqueLicenseNumberItShouldUpdateAgent() {
        UpdateAgentCommand updateAgentCommand = new UpdateAgentCommand();
        updateAgentCommand.setAgentId("100011");
        updateAgentCommand.setAuthorizePlansToSell(Sets.newHashSet(new PlanId("1000"), new PlanId("1001")));
        updateAgentCommand.setUserDetails(userDetails);
        updateAgentCommand.setAgentStatus(AgentStatus.ACTIVE);
        DesignationDto designationDto = new DesignationDto("Employee", "Employee");
        LocalDate trainingCompletionDate = new LocalDate(2015, 3, 24);
        updateAgentCommand.setAgentProfile(new AgentProfileDto("Mr", "Test", "Agent", 1111, "EMP002", trainingCompletionDate, designationDto));
        updateAgentCommand.setLicenseNumber(new LicenseNumberDto("LIC0001"));
        GeoDetailDto geoDetailDto = new GeoDetailDto(560068, "Bangalore", "India");
        updateAgentCommand.setContactDetail(new ContactDetailDto("9916971271", "9916971271", "9916971271", "abc@def.com", "Kormangala", "Kormangala", geoDetailDto));
        updateAgentCommand.setPhysicalAddress(new PhysicalAddressDto("Bomanahalli", "Bomanahalli", geoDetailDto));
        updateAgentCommand.setChannelType(new ChannelTypeDto("Broaker", "Broaker"));
        updateAgentCommand.setTeamDetail(new TeamDetailDto("TEAM001"));
        commandGateway.sendAndWait(updateAgentCommand);
    }
}
