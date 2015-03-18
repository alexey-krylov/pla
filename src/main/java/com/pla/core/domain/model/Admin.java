/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.application.agent.CreateAgentCommand;
import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.domain.model.agent.Agent;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.specification.TeamCodeIsUnique;
import com.pla.core.specification.TeamNameIsUnique;
import com.pla.core.domain.exception.TeamDomainException;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import static com.pla.core.domain.exception.AgentException.raiseAgentLicenseNumberUniqueException;

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
    public Team createTeam(boolean isTeamUnique, String teamId, String teamName, String teamCode,String regionCode,String branchCode
            ,String employeeId,LocalDate fromDate, String firstName, String lastName) {
        if (!isTeamUnique) {
            throw new TeamDomainException("Team name & Team Code already satisfied");
        }
        TeamLeader teamLeader = new TeamLeader(employeeId,firstName, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, fromDate);
        return new Team(teamId, teamName, teamCode, regionCode, branchCode, employeeId, teamLeaderFulfillment,Boolean.TRUE);
    }
    public Team updateTeamLead(Team team, String employeeId, String firstName, String lastName, LocalDate fromDate) {
        Team updatedTeam = team.assignTeamLeader(employeeId, firstName, lastName, fromDate);
        return updatedTeam;
    }

    public Agent createAgent(boolean isLicenseNumberUnique, CreateAgentCommand createAgentCommand) {
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException("Agent cannot be created as license number is in use");
        }
        Agent agent = Agent.createAgent(new AgentId(createAgentCommand.getAgentId()));
        return agent;
    }
}
