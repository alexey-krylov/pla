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

import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;


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
        team = admin.createTeam(isTeamUnique, "12345", "TEAMNAME", "TEAMCODE", "REGIONCODE","BRANCHCODE", "employeedId1",
                LocalDate.now(), "TLF", "TLL");
    }

    @Test
    public void testCreateTeamAndTeamLead() {
        admin.updateTeamLead(team, "aa", "employeedId2", "ss", new LocalDate(2015, 03, 17));
        Team updatedTeam = admin.updateTeamLead(team, "employeedId3", "NTLF", "NTLL", LocalDate.now());
        assertEquals("employeedId3", updatedTeam.getCurrentTeamLeader());
    }
    @Test
    public void testUpdateTeamLeaderFullFillment() {
        TeamLeader teamLeader = new TeamLeader("employeedId1", "Nischitha", "Kurunji");
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, new LocalDate(2015, 03, 18).minusDays(2));
        Set<TeamLeaderFulfillment> updatedTeamLeaderFulfillments = team.updateTeamLeaderFullfillment(team.getTeamLeaders(), teamLeaderFulfillment);
        Iterator<TeamLeaderFulfillment> iterator = updatedTeamLeaderFulfillments.iterator();
        assertEquals(new LocalDate(2015, 03, 18).minusDays(2), ((TeamLeaderFulfillment)iterator.next()).getFromDate());
    }
    @Test
    public void testGetCurrentTeamLeaderFulfillment() {
        assertEquals("employeedId1", team.getCurrentTeamLeaderFulfillment("12345").getTeamLeader().getEmployeeId());
    }
    @Test
    public void testCreateTeamLeaderFulfillment() {
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(new TeamLeader("12345678", "TEAMNAME", "TEAMCODE"),new LocalDate(2015, 03, 18).minusDays(3));
        assertEquals(teamLeaderFulfillment, team.createTeamLeaderFulfillment("12345678", "TEAMNAME", "TEAMCODE", new LocalDate(2015, 03, 18).minusDays(3)));
    }
    @Test
    public void testAssignTeamLeader() {
        assertEquals(team,team.assignTeamLeader("12345", "TEAMNAME", "TEAMCODE", LocalDate.now()));
    }
}
