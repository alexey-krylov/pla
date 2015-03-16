/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.query.BenefitFinder;
import com.pla.core.specification.BenefitIsUpdatable;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.core.specification.TeamCodeIsUnique;
import com.pla.core.specification.TeamNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
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
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TeamUnitTest {

    @Mock
    private TeamNameIsUnique teamNameIsUniqueName;

    @Mock
    private TeamCodeIsUnique teamCodeIsUnique;

    @Mock
    private BenefitFinder benefitFinder;

    @Mock
    private BenefitIsUpdatable benefitIsUpdatable;

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
   /* @Test
    public void testCreateTeamAndTeamLead() {
        Team team = admin.createTeam(teamNameIsUniqueName, teamCodeIsUnique, "552255", "gggg", "LggggggLL", "employeedId1",
                LocalDate.now(), LocalDate.now(),"www","PPP");
        String updatedName = "777";
        admin.updateTeamLead(team, "aa", "employeedId2", "ss", LocalDate.now());
        System.out.println("creat t lid :"+team.getCurrentTeamLeader());
        Team updatedTeam = admin.updateTeamLead(team, "vvv", "employeedId3", "ss", LocalDate.now());
        for(int i=0; i< updatedTeam.getTeamLeaders().size();i++)
        {
            TeamLeaderFulfillment teamLeaderFulfillment = updatedTeam.getTeamLeaders().get(i);
            System.out.println("From date : "+teamLeaderFulfillment.getFromDate()+" thru date"+teamLeaderFulfillment.getThruDate()+":: "+teamLeaderFulfillment.getTeamLeader().getEmployeeId());
        }
        assertEquals("777", updatedTeam.getCurrentTeamLeader());
       // System.out.println(updatedBenefit.getCurrentTeamLeader());
    }*/
}
