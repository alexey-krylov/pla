/*
 * Copyright (c) 3/5/15 4:20 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.dto.TeamDto;
import com.pla.core.query.TeamFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author: Nischitha
 * @since 1.0 18/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamIsAssociatedWithAgentTest {

    @Mock
    private TeamFinder teamFinder;


    @Test
    public void shouldReturnTrueWhenTeamAssociatedWithAgentName() {
        when(teamFinder.getActiveTeamCountByAgentAssociatedWithTeam(new TeamDto("team1", "team1","2324"))).thenReturn(1);
        TeamAssociatedWithAgent teamAssociatedWithAgent = new TeamAssociatedWithAgent(teamFinder);
        TeamDto teamDto = new TeamDto("team1", "team1","2324");
        boolean teamIsAssociatedWithAgent = teamAssociatedWithAgent.isSatisfiedBy(teamDto);
        assertTrue(teamIsAssociatedWithAgent);
    }

   @Test
    public void shouldReturnFalseWhenTeamAssociatedWithAgentName() {
       when(teamFinder.getActiveTeamCountByAgentAssociatedWithTeam(new TeamDto("team1", "team1","23123"))).thenReturn(0);
       TeamAssociatedWithAgent teamAssociatedWithAgent = new TeamAssociatedWithAgent(teamFinder);
       TeamDto teamDto = new TeamDto("team1", "team1","23123");
       boolean teamIsAssociatedWithAgent = teamAssociatedWithAgent.isSatisfiedBy(teamDto);
       assertFalse(teamIsAssociatedWithAgent);
    }
}
