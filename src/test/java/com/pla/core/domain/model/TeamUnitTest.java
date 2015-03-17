/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.specification.TeamCodeIsUnique;
import com.pla.core.specification.TeamNameIsUnique;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;


/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamUnitTest {

    @Mock
    private TeamNameIsUnique teamNameIsUniqueName;

    @Mock
    private TeamCodeIsUnique teamCodeIsUnique;
    private Admin admin;

    @Before
    public void setUp() {
        admin = new Admin();
    }

    @Test
    public void testCreateTeamAndTeamLead() {
        Team team = admin.createTeam(true, true, "12345", "TEAMNAME", "TEAMCODE", "employeedId1",
                LocalDate.now(),"TLF","TLL");
        admin.updateTeamLead(team, "aa", "employeedId2", "ss", LocalDate.now());
        Team updatedTeam = admin.updateTeamLead(team, "employeedId3", "NTLF", "NTLL", LocalDate.now());
        assertEquals("employeedId3", updatedTeam.getCurrentTeamLeader());
    }
    @Test
    public void testupdateTeamLeaderFullFillment() {
        Team team = admin.createTeam(true, true, "12345", "TEAMNAME", "TEAMCODE", "employeedId1",
                LocalDate.now(),"TLF","TLL");
        Team updatedTeam = team.updateTeamLeaderFullFillment("employeedId5","TLF","TLL", LocalDate.now());
        assertEquals("employeedId5", updatedTeam.getCurrentTeamLeader());
    }

}
