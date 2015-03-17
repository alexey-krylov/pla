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

    /*@Test
    public void givenABenefitNameItShouldCreateBenefit() {
        String name = "CI Benefit";
        when(benefitNameIsUnique.isSatisfiedBy(new BenefitName(name))).thenReturn(true);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        BenefitName benefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(name, benefitName.getBenefitName());
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void itShouldInactivateABenefit() {
        String name = "CI Benefit";
        when(benefitNameIsUnique.isSatisfiedBy(new BenefitName(name))).thenReturn(true);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, "1", name);
        benefit = admin.inactivateBenefit(benefit);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }*/
    @Test
    public void testCreateTeamAndTeamLead() {
        Team team = admin.createTeam(true, true, "12345", "TEAMNAME", "TEAMCODE", "employeedId1",
                LocalDate.now(),"TLF","TLL");
        String updatedName = "777";
        admin.updateTeamLead(team, "aa", "employeedId2", "ss", LocalDate.now());
        System.out.println("creat t lid :" + team.getCurrentTeamLeader());
        Team updatedTeam = admin.updateTeamLead(team, "employeedId3", "NTLF", "NTLL", LocalDate.now());
        for(int i=0; i< updatedTeam.getTeamLeaders().size();i++)
        {
            TeamLeaderFulfillment teamLeaderFulfillment = updatedTeam.getTeamLeaders().get(i);
            System.out.println("From date : "+teamLeaderFulfillment.getFromDate()+" thru date"+teamLeaderFulfillment.getThruDate()+":: "+teamLeaderFulfillment.getTeamLeader().getEmployeeId());
        }
        assertEquals("employeedId3", updatedTeam.getCurrentTeamLeader());
       // System.out.println(updatedBenefit.getCurrentTeamLeader());
    }
   /* @Test
    public void testExpireCurrentTeamLeadFullFillment() {
        Team team = admin.createTeam(teamNameIsUniqueName, teamCodeIsUnique, "12345", "TEAMNAME", "TEAMCODE", "employeedId1",
                LocalDate.now(),"TLF","TLL");
        Team updatedTeam = team.updateTeamLeaderFullFillment(employeedId1,"TLF","TLL");
        assertEquals(team.getCurrentTeam, team.expireCurrentTeamLeaderFullFillment();
        assertEquals("employeedId3", updatedTeam.getCurrentTeamLeader());
        // System.out.println(updatedBenefit.getCurrentTeamLeader());
    }*/

}
