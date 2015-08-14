/*
 * Copyright (c) 3/5/15 6:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *//*


package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.dto.CommissionTermDto;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.identifier.CommissionId;
import org.axonframework.commandhandling.gateway.CommandGateway;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

*/
/**
 * @author: Nischitha
 * @since 1.0 05/03/2015
 *//*

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class CommissionAcceptanceTest {

    private Logger logger = LoggerFactory.getLogger(CommissionAcceptanceTest.class);

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
    @ExpectedDatabase(value = "classpath:testdata/endtoend/commission/expectedcommissiondata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void itShouldCreateComissionForAGivenPlan() {
        CreateCommissionCommand createCommissionCommand = new CreateCommissionCommand();
        createCommissionCommand.setUserDetails(userDetails);
        createCommissionCommand.setPlanId("21831837");
        createCommissionCommand.setCommissionType(CommissionType.NORMAL);
        createCommissionCommand.setAvailableFor(CommissionDesignation.AGENT);
        Set<CommissionTermDto> commissionTerms = Sets.newHashSet();
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setStartYear(1);
        commissionTermDto.setEndYear(3);
        commissionTermDto.setCommissionTermType(CommissionTermType.RANGE);
        commissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        commissionTerms.add(commissionTermDto);
        createCommissionCommand.setCommissionTermSet(commissionTerms);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(createCommissionCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating commission", e);
        }
        assertTrue(isSuccess);

    }
    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/commission/expectedcommissiondata.xml")
    @ExpectedDatabase(value = "classpath:testdata/endtoend/commission/expectedupdatedcommissiondata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenCommissionIdWithCommissionTermsShouldUpdateCommission() {
        UpdateCommissionCommand updateCommissionCommand = new UpdateCommissionCommand();
        updateCommissionCommand.setPlanId("123232134");
        updateCommissionCommand.setCommissionId(new CommissionId("47474"));
        updateCommissionCommand.setUserDetails(userDetails);
        Set<CommissionTermDto> commissionTerms = Sets.newHashSet();
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setStartYear(5);
        commissionTermDto.setEndYear(6);
        commissionTermDto.setCommissionTermType(CommissionTermType.RANGE);
        commissionTermDto.setCommissionPercentage(new BigDecimal(37.90));
        commissionTerms.add(commissionTermDto);*/
/*
        CommissionTermDto anotherCommissionTermDto = new CommissionTermDto();
        anotherCommissionTermDto.setStartYear(1);
        anotherCommissionTermDto.setEndYear(3);
        anotherCommissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        commissionTerms.add(anotherCommissionTermDto);*//*

        updateCommissionCommand.setCommissionTermSet(commissionTerms);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(updateCommissionCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating commission", e);
        }
        assertTrue(isSuccess);

    }
}*/
