/*
 * Copyright (c) 3/5/15 4:20 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.domain.model.BenefitName;
import com.pla.core.query.BenefitFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitNameIsUniqueTest {

    @Mock
    private BenefitFinder benefitFinder;


    @Test
    public void should_return_true_when_benefit_name_unique() {
        when(benefitFinder.getBenefitCountByBenefitName("Death Benefit")).thenReturn(0);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitName benefitName = new BenefitName("Death Benefit");
        boolean alreadyExists = benefitNameIsUnique.isSatisfiedBy(benefitName);
        assertTrue(alreadyExists);
    }

    @Test
    public void should_return_false_when_benefit_name_not_unique() {
        when(benefitFinder.getBenefitCountByBenefitName("Death Benefit")).thenReturn(1);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitName benefitName = new BenefitName("Death Benefit");
        boolean alreadyExists = benefitNameIsUnique.isSatisfiedBy(benefitName);
        assertFalse(alreadyExists);
    }
}
