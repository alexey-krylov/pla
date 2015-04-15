/*
 * Copyright (c) 3/10/15 9:21 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.core.domain.model.BenefitName;
import com.pla.core.dto.BenefitDto;
import com.pla.core.query.BenefitFinder;
import com.pla.core.specification.BenefitIsAssociatedWithCoverage;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    private BenefitFinder benefitFinder;

    @Mock
    private IIdGenerator idGenerator;

    private BenefitService benefitService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage = new BenefitIsAssociatedWithCoverage(benefitFinder);
        benefitService = new BenefitService(adminRoleAdapter, benefitNameIsUnique, idGenerator, benefitIsAssociatedWithCoverage);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin();
    }


    @Test
    public void givenABenefitNameItShouldCreateBenefitWithActiveState() {
        String benefitId = "BE001";
        String name = "CI Benefit";
        when(idGenerator.nextId()).thenReturn(benefitId);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        BenefitDto benefitDto = new BenefitDto(benefitId, name);
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(true);
        Benefit benefit = benefitService.createBenefit(name, userDetails);
        BenefitName createdBenefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertNotNull(benefit);
        assertEquals(new BenefitId(benefitId), invokeGetterMethod(benefit, "getBenefitId"));
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
        assertEquals(name, createdBenefitName.getBenefitName());
    }

    @Test
    public void givenABenefitWithUpdatedNameItShouldUpdateBenefit() {
        Benefit benefit = getBenefit();
        String name = "CI Benefit";
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(0);
        BenefitDto benefitDto = new BenefitDto(benefit.getBenefitId().getBenefitId(), name);
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(true);
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
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Benefit updatedBenefit = benefitService.inactivateBenefit(benefit, userDetails);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(updatedBenefit, "getStatus"));
    }

    private Benefit getBenefit() {
        String name = "Accidental death benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = Boolean.TRUE;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        return benefit;
    }
}
