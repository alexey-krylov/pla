/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.query.BenefitFinder;
import com.pla.core.specification.BenefitIsUpdatable;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * @author: Samir
 * @since 1.0 12/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class AdminUnitTest {

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    @Mock
    private BenefitFinder benefitFinder;

    @Mock
    private BenefitIsUpdatable benefitIsUpdatable;

    private Admin admin;

    @Before
    public void setUp() {
        admin = new Admin();
    }

    @Test
    public void givenABenefitNameItShouldCreateBenefit() {
        String name = "CI Benefit";
        when(benefitNameIsUnique.isSatisfiedBy(new BenefitName(name))).thenReturn(true);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", new BenefitName(name));
        BenefitName benefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(name, benefitName.getBenefitName());
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void itShouldInactivateABenefit() {
        BenefitName benefitName = new BenefitName("CI Benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(true);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", benefitName);
        benefit = admin.inactivateBenefit(benefit);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    public void itShouldUpdateABenefit() {
        BenefitName benefitName = new BenefitName("CI Benefit");
        when(benefitNameIsUnique.isSatisfiedBy(benefitName)).thenReturn(true);
        when(benefitIsUpdatable.isSatisfiedBy(new BenefitId("1000"), benefitName)).thenReturn(true);
        when(benefitIsUpdatable.isGeneralizationOf(benefitNameIsUnique, benefitName)).thenReturn(true);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", benefitName);
        String updatedName = "Accidental Benefit";
        Benefit updatedBenefit = admin.updateBenefit(benefit, new BenefitName(updatedName), benefitNameIsUnique, benefitIsUpdatable);
        BenefitName updatedBenefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(updatedName, updatedBenefitName.getBenefitName());
    }
}
