/*
 * Copyright (c) 3/20/15 8:20 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.domain.model.agent.LicenseNumber;
import com.pla.core.dto.BenefitDto;
import com.pla.core.query.AgentFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author: Samir
 * @since 1.0 20/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class LicenseNumberIsUniqueUnitTest {

    @Mock
    private AgentFinder agentFinder;

    @Test
    public void shouldReturnTrueWhenBenefitNameUnique() {
        when(agentFinder.getAgentCountByLicenseNumber("LIC001")).thenReturn(0);
        LicenseNumber licenseNumber = new LicenseNumber("LIC001");
        AgentLicenseNumberIsUnique agentLicenseNumberIsUnique = new AgentLicenseNumberIsUnique(agentFinder);
        boolean alreadyExists = agentLicenseNumberIsUnique.isSatisfiedBy(licenseNumber);
        assertTrue(alreadyExists);
    }

    @Test
    public void shouldReturnFalseWhenBenefitNameNotUnique() {
        when(agentFinder.getAgentCountByLicenseNumber("LIC001")).thenReturn(1);
        LicenseNumber licenseNumber = new LicenseNumber("LIC001");
        AgentLicenseNumberIsUnique agentLicenseNumberIsUnique = new AgentLicenseNumberIsUnique(agentFinder);
        boolean alreadyExists = agentLicenseNumberIsUnique.isSatisfiedBy(licenseNumber);
        assertFalse(alreadyExists);
    }
}
