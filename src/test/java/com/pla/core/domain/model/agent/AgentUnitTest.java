/*
 * Copyright (c) 3/20/15 8:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.google.common.collect.Sets;
import com.pla.core.domain.exception.AgentException;
import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import com.pla.sharedkernel.identifier.PlanId;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author: Samir
 * @since 1.0 20/03/2015
 */
public class AgentUnitTest {

    Agent agent;

    @Before
    public void setUp() {
        agent = Agent.createAgent(new AgentId(1));
    }

    @Test
    public void itShouldCreateAgentWithProfileDetailWhenTrainingCompletionDateIsPast() {
        LocalDate trainingCompletionDate = LocalDate.now();
        agent = agent.createWithAgentProfile("First", "Agent", trainingCompletionDate, "Programmer", "Programmer");
        AgentProfile agentProfile = agent.getAgentProfile();
        assertEquals("First", agentProfile.getFirstName());
        assertEquals("Agent", agentProfile.getLastName());
        assertEquals(trainingCompletionDate, agentProfile.getTrainingCompleteOn());
        assertEquals(AgentStatus.ACTIVE, agent.getAgentStatus());
        assertEquals(OverrideCommissionApplicable.NO, agent.getOverrideCommissionApplicable());
    }

    @Test(expected = IllegalStateException.class)
    public void itShouldNotCreateAgentWhenTrainingCompletionDateIsFuture() {
        LocalDate trainingCompletionDate = LocalDate.now().plusDays(1);
        agent = agent.createWithAgentProfile("First", "Agent", trainingCompletionDate, "Programmer", "Programmer");
    }

    @Test
    public void agentShouldHaveProvisionToCollectOverrideCommissionIfDesignationIsBDE() {
        LocalDate trainingCompletionDate = LocalDate.now();
        agent = agent.createWithAgentProfile("First", "Agent", trainingCompletionDate, "BDE", "BDE");
        assertEquals(OverrideCommissionApplicable.YES, agent.getOverrideCommissionApplicable());
    }

    @Test
    public void itShouldCreateAgentWithLicenseNumber() {
        agent = agent.withLicenseNumber("AGENT0001");
        assertEquals(new LicenseNumber("AGENT0001"), agent.getLicenseNumber());
    }

    @Test
    public void itShouldCreateWithActiveTeamDetail() {
        agent = agent.withTeamDetail("TEAM001");
        assertEquals(new TeamDetail("TEAM001"), agent.getTeamDetail());
    }

    @Test
    public void itShouldUpdateWithContactDetailWhenAgentIsActive() {
        agent = agent.withContactDetail("991623044", "802574500", "8021345687", "abc@gmail.com", "bangalore", "kormangala", 560068, "India", "Bangalore");
        ContactDetail contactDetail = agent.getContactDetail();
        assertEquals("991623044", contactDetail.getMobileNumber());
        assertEquals("802574500", contactDetail.getHomePhoneNumber());
        assertEquals("8021345687", contactDetail.getWorkPhoneNumber());
        assertEquals("abc@gmail.com", contactDetail.getEmailAddress().getEmail());
        assertEquals("bangalore", contactDetail.getAddressLine1());
        assertEquals("kormangala", contactDetail.getAddressLine2());
        assertEquals(Integer.valueOf(560068), contactDetail.getGeoDetail().getPostalCode());
        assertEquals("India", contactDetail.getGeoDetail().getProvince());
        assertEquals("Bangalore", contactDetail.getGeoDetail().getCity());
    }

    @Test(expected = AgentException.class)
    public void itsShouldNotUpdateContactDetailWhenAgentIsInactive() {
        agent = agent.updateStatus(AgentStatus.INACTIVE);
        agent = agent.withContactDetail("991623044", "802574500", "8021345687", "abc@gmail.com", "bangalore", "kormangala", 560068, "India", "Bangalore");
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdateContactDetailWhenAgentIsTerminated() {
        agent = agent.updateStatus(AgentStatus.TERMINATED);
        agent = agent.withContactDetail("991623044", "802574500", "8021345687", "abc@gmail.com", "bangalore", "kormangala", 560068, "India", "Bangalore");
    }


    @Test
    public void itShouldUpdateWithPhysicalAddressWhenAgentIsActive() {
        agent = agent.withPhysicalAddress("AshokNagar", "Lane1", 761102, "India", "Berhampur");
        PhysicalAddress physicalAddress = agent.getPhysicalAddress();
        assertEquals("AshokNagar", physicalAddress.getPhysicalAddressLine1());
        assertEquals("Lane1", physicalAddress.getPhysicalAddressLine2());
        assertEquals(Integer.valueOf(761102), physicalAddress.getPhysicalGeoDetail().getPostalCode());
        assertEquals("India", physicalAddress.getPhysicalGeoDetail().getProvince());
        assertEquals("Berhampur", physicalAddress.getPhysicalGeoDetail().getCity());
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdatePhysicalAddressWhenAgentIsInactive() {
        agent = agent.updateStatus(AgentStatus.INACTIVE);
        agent = agent.withPhysicalAddress("AshokNagar", "Lane1", 761102, "India", "Berhampur");
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdatePhysicalAddressWhenAgentIsTerminated() {
        agent = agent.updateStatus(AgentStatus.TERMINATED);
        agent = agent.withPhysicalAddress("AshokNagar", "Lane1", 761102, "India", "Berhampur");
    }

    @Test
    public void itShouldUpdateAgentWithPlans() {
        Set<PlanId> plans = Sets.newHashSet(new PlanId("PLAN001"), new PlanId("PLAN002"));
        agent = agent.withPlans(plans);
        assertEquals(plans, agent.getAuthorizePlansToSell());
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdatePlanWhenAgentIsInactive() {
        agent = agent.updateStatus(AgentStatus.INACTIVE);
        Set<PlanId> plans = Sets.newHashSet(new PlanId("PLAN001"), new PlanId("PLAN002"));
        agent = agent.withPlans(plans);
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdatePlanWhenAgentIsTerminated() {
        agent = agent.updateStatus(AgentStatus.TERMINATED);
        Set<PlanId> plans = Sets.newHashSet(new PlanId("PLAN001"), new PlanId("PLAN002"));
        agent = agent.withPlans(plans);
    }

    @Test
    public void itShouldUpdateAgentWithChannelDetail() {
        agent = agent.withChannelType("BRK001", "Broker");
        assertEquals("BRK001", agent.getChannelType().getChannelCode());
        assertEquals("Broker", agent.getChannelType().getChannelName());
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdateChannelTypeWhenAgentIsInactive() {
        agent = agent.updateStatus(AgentStatus.INACTIVE);
        agent = agent.withChannelType("BRK001", "Broker");
    }

    @Test(expected = AgentException.class)
    public void itShouldNotUpdateChannelTypeWhenAgentIsTerminated() {
        agent = agent.updateStatus(AgentStatus.TERMINATED);
        agent = agent.withChannelType("BRK001", "Broker");
    }

    @Test
    public void itShouldMakeAgentEligibleForOverrideCommissionIfAgentIsBDE() {
        agent = agent.applyOverrideCommissionEligibility("BDE", "BDE");
        assertEquals(OverrideCommissionApplicable.YES, agent.getOverrideCommissionApplicable());
    }

    @Test
    public void itShouldInactivateAgent() {
        agent = agent.inactivate();
        assertEquals(AgentStatus.INACTIVE, agent.getAgentStatus());
    }

    @Test
    public void itShouldTerminateAgent() {
        agent = agent.terminate();
        assertEquals(AgentStatus.TERMINATED, agent.getAgentStatus());
    }

    @Test
    public void itShouldActivateAgent() {
        agent = agent.activate();
        assertEquals(AgentStatus.ACTIVE, agent.getAgentStatus());
    }
}
