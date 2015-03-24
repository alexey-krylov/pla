/*
 * Copyright (c) 3/20/15 9:21 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * @author: Samir
 * @since 1.0 20/03/2015
 */
public class AgentProfileUnitTest {

    AgentProfile agentProfile;

    @Before
    public void setUp() {
        Agent agent = Agent.createAgent(new AgentId("1"));
        agentProfile = agent.createWithAgentProfile("First", "Agent", LocalDate.now(), "Programmer", "Programmer").getAgentProfile();
    }


    @Test
    public void itShouldUpdateAgentProfileWithTitle() {
        agentProfile = agentProfile.withTitle("Mr");
        assertEquals("Mr", agentProfile.getTitle());
    }

    @Test
    public void itShouldUpdateAgentProfileWithNrcNumber() {
        agentProfile = agentProfile.withNrcNumber(999999999);
        assertEquals(Integer.valueOf(999999999), agentProfile.getNrcNumber());
    }

    @Test
    public void itShouldUpdateAgentProfileWithEmployeeId() {
        agentProfile = agentProfile.withEmployeeId("EMP001");
        assertEquals("EMP001", agentProfile.getEmployeeId());
    }
}
