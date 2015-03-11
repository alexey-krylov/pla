/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitException;
import com.pla.core.domain.exception.TeamException;
import com.pla.core.specification.BenefitNameIsUnique;
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

    private String userName;

    public Admin(String userName) {
        this.userName = userName;

    }

    public Benefit createBenefit(BenefitNameIsUnique benefitNameIsUnique, String benefitId, String name) {
        BenefitName benefitName = new BenefitName(name);
        if (!benefitNameIsUnique.isSatisfiedBy(benefitName)) {
            throw new BenefitException("Benefit name already satisfied");
        }
        Benefit benefit = new Benefit(benefitId, benefitName, BenefitStatus.ACTIVE);
        return benefit;
    }

    public Benefit updateBenefit(Benefit benefit,String name){
        BenefitName benefitName = new BenefitName(name);
        benefit = benefit.updateBenefitName(benefitName);
        return benefit;
    }

    public Benefit inactivateBenefit(Benefit benefit){
        benefit = benefit.inActivate();
        return benefit;
    }
    public Team createTeam(TeamNameIsUnique teamNameIsUnique, String teamId, String name, String code
            ,String employeeId,LocalDate fromDate, LocalDate thruDate, String firstname, String lastName) {
        TeamName teamName = new TeamName(name, code);
        if (!teamNameIsUnique.isSatisfiedBy(teamName)) {
            throw  new TeamException("Team Name and Team Code already exists");
        }
        TeamLeader teamLeader = new TeamLeader(employeeId, fromDate, thruDate, firstname, lastName);
        Team team = new Team(teamId, name,code, teamLeader);
        return team;
    }
}
