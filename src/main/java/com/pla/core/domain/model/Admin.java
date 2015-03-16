/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.specification.TeamCodeIsUnique;
import com.pla.core.specification.TeamNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@ValueObject
public class Admin {


    public Benefit createBenefit(boolean isUniqueBenefitName, String benefitId, String benefitName) {
        if (!isUniqueBenefitName) {
            throw new BenefitDomainException("Benefit name already satisfied");
        }
        return new Benefit(new BenefitId(benefitId), new BenefitName(benefitName), BenefitStatus.ACTIVE);
    }

    public Benefit updateBenefit(Benefit benefit, String newBenefitName, boolean benefitIsUpdatable) {
        if (!benefitIsUpdatable) {
            throw new BenefitDomainException("Benefit is associated with active coverage");
        }
        Benefit updatedBenefit = benefit.updateBenefitName(new BenefitName(newBenefitName));
        return updatedBenefit;
    }

    public Benefit inactivateBenefit(Benefit benefit) {
        Benefit updatedBenefit = benefit.inActivate();
        return updatedBenefit;
    }

    public Team createTeam(boolean teamNameIsUnique, boolean teamCodeIsUnique, String teamId, String name, String code
            , String employeeId, LocalDate fromDate, LocalDate thruDate, String firstname, String lastName) {
        TeamName teamName = new TeamName(name);
        TeamCode teamCode = new TeamCode(code);
        TeamLeader teamLeader = new TeamLeader(employeeId, firstname, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, fromDate);
        return new Team(teamId, teamCode, teamName, employeeId, teamLeaderFulfillment, Boolean.TRUE);
    }

    public Team updateTeamLead(Team team, String employeeId, String firstName, String lastName, LocalDate fromDate) {
        Team updatedTeam = team.assignTeamLeader(employeeId, firstName, lastName, fromDate);
        return updatedTeam;
    }
}
