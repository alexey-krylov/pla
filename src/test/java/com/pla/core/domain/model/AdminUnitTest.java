/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.application.agent.CreateAgentCommand;
import com.pla.core.domain.exception.AgentException;
import com.pla.core.domain.model.agent.Agent;
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
    private BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage;

    private Admin admin;

    @Before
    public void setUp() {
        admin = new Admin();
    }

    @Test
    public void givenABenefitNameItShouldCreateBenefit() {
        String name = "CI Benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = true;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        BenefitName benefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(name, benefitName.getBenefitName());
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void itShouldInactivateABenefit() {
        String name = "CI Benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = true;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        benefit = admin.inactivateBenefit(benefit);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void itShouldUpdateABenefit() {
        String name = "CI Benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = true;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        boolean isUpdatable = true;
        when(benefitIsAssociatedWithCoverage.isSatisfiedBy(benefitDto)).thenReturn(isUpdatable);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        String updatedName = "Accidental Benefit";
        Benefit updatedBenefit = admin.updateBenefit(benefit, updatedName, isUpdatable);
        BenefitName updatedBenefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(updatedName, updatedBenefitName.getBenefitName());
    }

    @Test(expected = AgentException.class)
    public void itShouldNotCreateAgentWhenLicenseNumberIsNotUnique() {
        Agent agent = admin.createAgent(false,new CreateAgentCommand());
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdateAgentWhenLicenseNumberIsNotUnique() {
        Agent agent = admin.createAgent(false,new CreateAgentCommand());
    }
}
