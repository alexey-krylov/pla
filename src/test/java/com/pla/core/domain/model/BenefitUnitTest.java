/*
 * Copyright (c) 3/10/15 3:31 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitException;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitUnitTest {

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    @Test
    public void statusShouldBeActiveOnCreateOfNewBenefit() {
        Admin admin = new Admin("");
        String name = "Accidental death benefit";
        BenefitName benefitName = new BenefitName("Accidental death benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(Boolean.TRUE);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        assertEquals(BenefitStatus.ACTIVE, benefit.getStatus());
    }

    @Test
    public void statusOfBenefitShouldBeInUse() {
        Admin admin = new Admin("");
        String name = "Accidental death benefit";
        BenefitName benefitName = new BenefitName("Accidental death benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(Boolean.TRUE);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        benefit = benefit.markAsUsed();
        assertEquals(BenefitStatus.INUSE, benefit.getStatus());
    }


    @Test(expected = BenefitException.class)
    public void markingAnInactivatedBenefitShouldThrowExceptionAndStatusShouldBeInactive() {
        Admin admin = new Admin("");
        String name = "Accidental death benefit";
        BenefitName benefitName = new BenefitName("Accidental death benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(Boolean.TRUE);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        benefit = admin.inactivateBenefit(benefit);
        benefit = benefit.markAsUsed();
        assertEquals(BenefitStatus.INACTIVE, benefit.getStatus());
    }

    @Test(expected = BenefitException.class)
    public void inactivatingABenefitWithInUseStatusThrowExceptionAndStatusShouldBeInUse() {
        Admin admin = new Admin("");
        String name = "Accidental death benefit";
        BenefitName benefitName = new BenefitName("Accidental death benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(Boolean.TRUE);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        benefit = benefit.markAsUsed();
        benefit = admin.inactivateBenefit(benefit);
        assertEquals(BenefitStatus.INUSE, benefit.getStatus());
    }
}
