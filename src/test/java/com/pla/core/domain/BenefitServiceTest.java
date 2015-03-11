/*
 * Copyright (c) 3/10/15 9:21 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain;

import com.pla.core.application.CreateBenefitCommand;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.BenefitName;
import com.pla.core.domain.service.AdminRoleAdapter;
import com.pla.core.domain.service.BenefitService;
import com.pla.core.specification.BenefitNameIsUnique;

import static org.junit.Assert.*;

import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitServiceTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    @Mock
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Mock
    private IIdGenerator idGenerator;

    private BenefitService benefitService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        benefitService = new BenefitService(adminRoleAdapter, benefitNameIsUnique, jpaRepositoryFactory, idGenerator);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin("");
    }


    @Test
    public void givenACreateBenefitCommandShouldCreateBenefitWithActiveStatus() {
        String benefitId = "BE001";
        CreateBenefitCommand createBenefitCommand = new CreateBenefitCommand();
        createBenefitCommand.setUserDetails(userDetails);
        createBenefitCommand.setBenefitName("CI Benefit");
        BenefitName benefitName = new BenefitName(createBenefitCommand.getBenefitName());
        when(idGenerator.nextId()).thenReturn(benefitId);
        when(adminRoleAdapter.userToAdmin(createBenefitCommand.getUserDetails())).thenReturn(admin);
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(true);
        Benefit benefit = benefitService.createBenefit(createBenefitCommand);
        BenefitName createdBenefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(benefitId, invokeGetterMethod(benefit, "getBenefitId"));
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
        assertEquals(createBenefitCommand.getBenefitName(), createdBenefitName.getBenefitName());
    }
}
