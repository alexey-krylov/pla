/*
 * Copyright (c) 3/10/15 9:21 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.BenefitName;
import com.pla.core.specification.BenefitIsUpdatable;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitServiceUnitTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    @Mock
    private BenefitIsUpdatable benefitIsUpdatable;

    @Mock
    private IIdGenerator idGenerator;

    @InjectMocks
    private BenefitService benefitService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin();
    }


    @Test
    public void givenABenefitNameItShouldCreateBenefitWithActiveState() {
        String benefitId = "BE001";
        String name = "CI Benefit";
        BenefitName benefitName = new BenefitName(name);
        when(idGenerator.nextId()).thenReturn(benefitId);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(true);
        Benefit benefit = benefitService.createBenefit(name, userDetails);
        BenefitName createdBenefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertNotNull(benefit);
        assertEquals(benefitId, invokeGetterMethod(benefit, "getBenefitId"));
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
        assertEquals(name, createdBenefitName.getBenefitName());
    }

    @Test
    public void givenABenefitWithUpdatedNameItShouldUpdateBenefit() {
        Benefit benefit = getBenefit();
        String name = "CI Benefit";
        BenefitName benefitName = new BenefitName(name);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(benefitIsUpdatable.isSatisfiedBy("1", benefitName)).thenReturn(true);
        when(benefitIsUpdatable.isGeneralizationOf(benefitNameIsUnique, benefitName)).thenReturn(true);
        Benefit updatedBenefit = benefitService.updateBenefit(benefit, name, userDetails);
        BenefitName updatedBenefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(updatedBenefit, "getStatus"));
        assertEquals(name, updatedBenefitName.getBenefitName());
    }

    @Test
    public void givenABenefitWhenMarkAsUsedItShouldBeInUsedStatus() {
        Benefit benefit = getBenefit();
        Benefit updatedBenefit = benefitService.markBenefitAsUsed(benefit);
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(updatedBenefit, "getStatus"));
    }


    @Test
    public void givenABenefitItShouldInactivateBenefit() {
        Benefit benefit = getBenefit();
        String name = "CI Benefit";
        BenefitName benefitName = new BenefitName(name);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Benefit updatedBenefit = benefitService.inactivateBenefit(benefit, userDetails);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(updatedBenefit, "getStatus"));
    }

    private Benefit getBenefit() {
        String name = "Accidental death benefit";
        BenefitName benefitName = new BenefitName(name);
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(Boolean.TRUE);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        return benefit;
    }
}
