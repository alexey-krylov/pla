/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.specification.TeamIsUnique;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamUnitTest {

    @Mock
    private TeamIsUnique teamNameIsUnique;

    private Admin admin;
    private Team team;

    @Before
    public void setUp() {
        boolean isTeamUnique = true;
        admin = new Admin();
        team = admin.createTeam(isTeamUnique, "12345", "TEAMNAME", "TEAMCODE", "REGIONCODE", "BRANCHCODE", "employeedId1", LocalDate.now(), "TLF", "TLL");
    }

    @Test
    public void testCreateTeamAndTeamLead() {
        admin.updateTeamLead(team, "aa", "employeedId2", "ss", LocalDate.now().plusDays(1));
        Team updatedTeam = admin.updateTeamLead(team, "employeedId3", "NTLF", "NTLL", LocalDate.now().plusDays(2));
        assertEquals("employeedId3", updatedTeam.getCurrentTeamLeader());
    }

    @Test
    public void itShouldAssignANewTeamLeaderAndExpireTheExistingTeamLeader() {
        TeamLeaderFulfillment teamLeaderFulfillment = Team.createTeamLeaderFulfillment("EMP001", "Nischitha", "Kurunji", LocalDate.now());
        Team team = new Team("TEAM001", "Health Insurance Team ", "HIT001", "REG01", "BRA001", "EMP001", teamLeaderFulfillment, Boolean.TRUE);
        Team updatedTeam = team.assignTeamLeader("EMP002", "Samir", "Padhy", LocalDate.now().plusDays(3));
        TeamLeaderFulfillment expiredTeamLeaderFulfillment = updatedTeam.getTeamLeaderFulfillmentForATeamLeader("EMP001");
        assertTrue(expiredTeamLeaderFulfillment != null);
        assertEquals("EMP002", updatedTeam.getCurrentTeamLeader());
        assertEquals(LocalDate.now().plusDays(2), expiredTeamLeaderFulfillment.getThruDate());

    }

    @Test
    public void itShouldReturnTeamLeaderFulfillmentForAGivenTeamLeaderId() {
        TeamLeaderFulfillment teamLeaderFulfillment = Team.createTeamLeaderFulfillment("EMP001", "Nischitha", "Kurunji", LocalDate.now());
        Team team = new Team("TEAM001", "Health Insurance Team ", "HIT001", "REG01", "BRA001", "EMP001", teamLeaderFulfillment, Boolean.TRUE);
        Team updatedTeam = team.assignTeamLeader("EMP002", "Samir", "Padhy", LocalDate.now().plusDays(3));
        TeamLeaderFulfillment teamLeaderFulfillmentById = updatedTeam.getTeamLeaderFulfillmentForATeamLeader("EMP001");
        assertEquals("EMP001", teamLeaderFulfillmentById.getTeamLeader().getEmployeeId());
    }

    @Test
    public void testCreateTeamLeaderFulfillment() {
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(new TeamLeader("12345678", "TEAMNAME", "TEAMCODE"), new LocalDate(2015, 03, 18).minusDays(1));
        assertEquals(teamLeaderFulfillment, Team.createTeamLeaderFulfillment("12345678", "TEAMNAME", "TEAMCODE", new LocalDate(2015, 03, 18).minusDays(1)));
    }


}

