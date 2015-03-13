/*
 * Copyright (c) 3/10/15 3:31 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitUnitTest {

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    private Benefit benefit;

    private Admin admin;

    private String name = "Accidental death benefit";

    @Before
    public void setUp() {
        admin = new Admin();
        BenefitName benefitName = new BenefitName("Accidental death benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(Boolean.TRUE);
        benefit = admin.createBenefit(benefitNameIsUnique, "1", benefitName);
    }

    @Test
    public void statusShouldBeActiveOnCreateOfNewBenefit() {
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void statusOfBenefitShouldBeInUse() {
        benefit = benefit.markAsUsed();
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(benefit, "getStatus"));
    }


    @Test(expected = BenefitDomainException.class)
    public void markingAnInactivatedBenefitShouldThrowExceptionAndStatusShouldBeInactive() {
        benefit = admin.inactivateBenefit(benefit);
        benefit = benefit.markAsUsed();
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test(expected = BenefitDomainException.class)
    public void inactivatingABenefitWithInUseStatusThrowExceptionAndStatusShouldBeInUse() {
        benefit = benefit.markAsUsed();
        benefit = admin.inactivateBenefit(benefit);
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test(expected = BenefitDomainException.class)
    public void benefitInUsedStatusShouldThrowExceptionOnUpdatingNameAndNameShouldBeUnchanged() {
        benefit = benefit.markAsUsed();
        String updatedName = "CI Benefit";
        benefit = benefit.updateBenefitName(new BenefitName(updatedName));
        BenefitName benefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(name, benefitName.getBenefitName());
    }

    @Test
    public void benefitInActiveStatusShouldGetUpdatedWithNewName() {
        String updatedName = "CI Benefit";
        Benefit updatedBenefit = benefit.updateBenefitName(new BenefitName(updatedName));;
        BenefitName benefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(updatedName, benefitName.getBenefitName());
    }
}
