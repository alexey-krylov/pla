/*
 * Copyright (c) 3/5/15 6:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
import com.pla.core.domain.service.AdminRoleAdapter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class CreateBenefitETETest {

    private Logger logger = LoggerFactory.getLogger(CreateBenefitETETest.class);
    
    @Autowired
    private CommandGateway commandGateway;

    @Test
    @ExpectedDatabase(value = "classpath:testdata/endtoend/expectedbenefitdata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenABenefitNameItShouldCreateBenefit() {
        UserLoginDetailDto userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
        List<String> permissions = Lists.newArrayList();
        permissions.add("ROLE_ADMIN");
        userLoginDetailDto = userLoginDetailDto.populateAuthorities(permissions);
        CreateBenefitCommand createBenefitCommand = new CreateBenefitCommand();
        createBenefitCommand.setBenefitName("Death Benefit");
        createBenefitCommand.setUserDetails(userLoginDetailDto);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(createBenefitCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating benefit",e);
        }
        assertTrue(isSuccess);

    }
}
