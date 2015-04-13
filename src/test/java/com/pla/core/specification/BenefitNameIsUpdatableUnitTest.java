/*
 * Copyright (c) 3/11/15 1:15 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.dto.BenefitDto;
import com.pla.core.query.BenefitFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitNameIsUpdatableUnitTest {

    @Mock
    private BenefitFinder benefitFinder;

    @Test
    public void shouldReturnTrueWhenBenefitIsNotAssociatedWithCoverage() {
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(0);
        BenefitIsAssociatedWithCoverage benefitNameIsUpdatable = new BenefitIsAssociatedWithCoverage(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", "CI Benefit");
        boolean benefitNameUpdatable = benefitNameIsUpdatable.isSatisfiedBy(benefitDto);
        assertTrue(benefitNameUpdatable);
    }

    @Test
    public void shouldReturnFalseWhenBenefitIsAssociateWithActiveCoverage() {
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(1);
        BenefitIsAssociatedWithCoverage benefitNameIsUpdatable = new BenefitIsAssociatedWithCoverage(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", "CI Benefit");
        boolean benefitNameUpdatable = benefitNameIsUpdatable.isSatisfiedBy(benefitDto);
        assertFalse(benefitNameUpdatable);
    }

    @Test
    public void shouldReturnTrueIfBenefitIsUniqueAndNotAssociateWithCoverage() {
        String benefitName = "CI Benefit";
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(0);
        when(benefitFinder.getBenefitCountByBenefitName(benefitName,"B001")).thenReturn(0);
        BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage = new BenefitIsAssociatedWithCoverage(benefitFinder);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", benefitName);
        boolean isUpdatable = benefitIsAssociatedWithCoverage.And(benefitNameIsUnique).isSatisfiedBy(benefitDto);
        assertTrue(isUpdatable);
    }

    @Test
    public void shouldReturnFalseIfBenefitIsUniqueAndAssociateWithCoverage() {
        String benefitName = "CI Benefit";
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(1);
        when(benefitFinder.getBenefitCountByBenefitName(benefitName,"B001")).thenReturn(0);
        BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage = new BenefitIsAssociatedWithCoverage(benefitFinder);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", benefitName);
        boolean isUpdatable = benefitIsAssociatedWithCoverage.And(benefitNameIsUnique).isSatisfiedBy(benefitDto);
        assertFalse(isUpdatable);
    }

    @Test
    public void shouldReturnFalseIfBenefitIsNotUniqueAndNotAssociateWithCoverage() {
        String benefitName = "CI Benefit";
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(1);
        when(benefitFinder.getBenefitCountByBenefitName(benefitName,"B001")).thenReturn(1);
        BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage = new BenefitIsAssociatedWithCoverage(benefitFinder);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", benefitName);
        boolean isUpdatable = benefitIsAssociatedWithCoverage.And(benefitNameIsUnique).isSatisfiedBy(benefitDto);
        assertFalse(isUpdatable);
    }
}
