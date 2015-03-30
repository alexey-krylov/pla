/*
 * Copyright (c) 3/5/15 4:20 PM .NthDimenzion,Inc - All Rights Reserved
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
 * @since 1.0 05/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitNameIsUniqueTest {

    @Mock
    private BenefitFinder benefitFinder;


    @Test
    public void shouldReturnTrueWhenBenefitNameUnique() {
        when(benefitFinder.getBenefitCountByBenefitName("Death Benefit")).thenReturn(0);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", "Death Benefit");
        boolean alreadyExists = benefitNameIsUnique.isSatisfiedBy(benefitDto);
        assertTrue(alreadyExists);
    }

    @Test
    public void shouldReturnFalseWhenBenefitNameNotUnique() {
        when(benefitFinder.getBenefitCountByBenefitName("Death Benefit")).thenReturn(1);
        BenefitNameIsUnique benefitNameIsUnique = new BenefitNameIsUnique(benefitFinder);
        BenefitDto benefitDto = new BenefitDto("1", "Death Benefit");
        boolean alreadyExists = benefitNameIsUnique.isSatisfiedBy(benefitDto);
        assertFalse(alreadyExists);
    }
}
