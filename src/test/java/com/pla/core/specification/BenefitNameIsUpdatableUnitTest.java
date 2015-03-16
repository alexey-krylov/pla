/*
 * Copyright (c) 3/11/15 1:15 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.google.common.collect.Maps;
import com.pla.core.domain.model.BenefitId;
import com.pla.core.domain.model.BenefitName;
import com.pla.core.query.BenefitFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

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
        BenefitIsUpdatable benefitNameIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        boolean benefitNameUpdatable = benefitNameIsUpdatable.isSatisfiedBy(new BenefitId("1"));
        assertTrue(benefitNameUpdatable);
    }

    @Test
    public void shouldReturnFalseWhenBenefitIsAssociateWithActiveCoverage() {
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(1);
        BenefitIsUpdatable benefitNameIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        boolean benefitNameUpdatable = benefitNameIsUpdatable.isSatisfiedBy(new BenefitId("1"));
        assertFalse(benefitNameUpdatable);
    }

    @Test
    public void shouldReturnTrueIfBenefitIsUniqueAndNotAssociateWithCoverage() {
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(0);
        when(benefitFinder.getBenefitCountByBenefitName("CI Benefit")).thenReturn(0);
        BenefitIsUpdatable benefitIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        boolean isUpdatable = benefitIsUpdatable.And(benefitNameIsUnique).isSatisfiedBy(new BenefitId("1"), new BenefitName("CI Benefit"));
        assertTrue(isUpdatable);
    }

    @Test
    public void shouldReturnFalseIfBenefitIsUniqueAndAssociateWithCoverage() {
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(1);
        when(benefitFinder.getBenefitCountByBenefitName("CI Benefit")).thenReturn(0);
        BenefitIsUpdatable benefitIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        boolean isUpdatable = benefitIsUpdatable.And(benefitNameIsUnique).isSatisfiedBy(new BenefitId("1"), new BenefitName("CI Benefit"));
        assertFalse(isUpdatable);
    }

    @Test
    public void shouldReturnFalseIfBenefitIsNotUniqueAndNotAssociateWithCoverage() {
        when(benefitFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(0);
        when(benefitFinder.getBenefitCountByBenefitName("CI Benefit")).thenReturn(1);
        BenefitIsUpdatable benefitIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        boolean isUpdatable = benefitIsUpdatable.And(benefitNameIsUnique).isSatisfiedBy(new BenefitId("1"), new BenefitName("CI Benefit"));
        assertFalse(isUpdatable);
    }
}
