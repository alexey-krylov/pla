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

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;


    @Test
    public void shouldReturnTrueWhenBenefitNameIsDifferent() {
        Map<String,Object> benefitMap = Maps.newHashMap();
        benefitMap.put("benefitName","CI Benefit");
        when(benefitFinder.findBenefitById("1")).thenReturn(benefitMap);
        BenefitIsUpdatable benefitNameIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        boolean benefitNameUpdatable = benefitNameIsUpdatable.isSatisfiedBy(new BenefitId("1"),new BenefitName("Health Benefit"));
        assertTrue(benefitNameUpdatable);
    }

    @Test
    public void shouldReturnFalseWhenBenefitNameIsSame() {
        Map<String,Object> benefitMap = Maps.newHashMap();
        benefitMap.put("benefitName","Health Benefit");
        when(benefitFinder.findBenefitById("1")).thenReturn(benefitMap);
        BenefitIsUpdatable benefitNameIsUpdatable = new BenefitIsUpdatable(benefitFinder);
        boolean benefitNameUpdatable = benefitNameIsUpdatable.isSatisfiedBy(new BenefitId("1"),new BenefitName("Health Benefit"));
        assertFalse(benefitNameUpdatable);
    }
}
