/*
 * Copyright (c) 3/5/15 6:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.*;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
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

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class BenefitAcceptanceTest {

    private Logger logger = LoggerFactory.getLogger(BenefitAcceptanceTest.class);

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
    @ExpectedDatabase(value = "classpath:testdata/endtoend/benefit/expectedbenefitdata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenABenefitNameItShouldCreateBenefit() {
        CreateBenefitCommand createBenefitCommand = new CreateBenefitCommand(userDetails, "Accidental Death Benefit");
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(createBenefitCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating benefit", e);
        }
        assertTrue(isSuccess);

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/benefit/testdataforupdatebenefit.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/benefit/expectedupdatedbenefitdata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/benefit/testdataforupdatebenefit.xml",type = DatabaseOperation.DELETE)
    public void givenAnActiveBenefitWithNewNameItShouldUpdateBenefit() {
        UpdateBenefitCommand updateBenefitCommand = new UpdateBenefitCommand("1", "CI Benefit", userDetails);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(updateBenefitCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating benefit", e);
        }
        assertTrue(isSuccess);
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/benefit/testdataforupdatebenefit.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/benefit/expecteddataformarkingbenefitasused.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/benefit/testdataforupdatebenefit.xml",type = DatabaseOperation.DELETE)
    public void givenAnActiveBenefitItShouldMarkAsUsed() {
        MarkBenefitAsUsedCommand markBenefitAsUsedCommand = new MarkBenefitAsUsedCommand("2");
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(markBenefitAsUsedCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in marking benefit as used", e);
        }
        assertTrue(isSuccess);
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/benefit/testdataforupdatebenefit.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/benefit/expectedinactivebenefitdata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/benefit/testdataforupdatebenefit.xml",type = DatabaseOperation.DELETE)
    public void givenABenefitItShouldInactivate(){
         InactivateBenefitCommand inactivateBenefitCommand = new InactivateBenefitCommand("1",userDetails);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(inactivateBenefitCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in inactivating benefit", e);
        }
        assertTrue(isSuccess);
    }
}
