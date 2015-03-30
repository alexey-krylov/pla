/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.exception.TeamDomainException;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.Set;

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

    public Coverage createCoverage(boolean isUniqueCoverageName, String coverageId, String coverageName,String description,  Set<Benefit> benefits) {
        if (!isUniqueCoverageName) {
            throw new CoverageException("Coverage name already satisfied");
        }
        Coverage  coverage = new Coverage(new CoverageId(coverageId), new CoverageName(coverageName), benefits, CoverageStatus.ACTIVE);
        if (description != null)
            coverage = coverage.updateDescription(description);
        return coverage;
    }

    public Coverage updateCoverage(Coverage coverage, String newCoverageName, Set<Benefit> benefits, boolean isCoverageNameUnique) {
        if (!isCoverageNameUnique) {
            throw new CoverageException("Coverage name already satisfied");
        }
        return coverage.updateCoverageName(newCoverageName).updateBenefit(benefits);
    }

    public Coverage inactivateCoverage(Coverage coverage) {
        Coverage deactivatedCoverage = coverage.deactivate();
        return deactivatedCoverage;
    }


    public Team createTeam(boolean isTeamUnique, String teamId, String teamName, String teamCode, String regionCode, String branchCode
            , String employeeId, LocalDate fromDate, String firstName, String lastName) {
        if (!isTeamUnique) {
            throw new TeamDomainException("Team name and Team Code already satisfied");
        }
        TeamLeader teamLeader = new TeamLeader(employeeId, firstName, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, fromDate);
        return new Team(teamId, teamName, teamCode, regionCode, branchCode, employeeId, teamLeaderFulfillment, Boolean.TRUE);
    }

    public Team updateTeamLead(Team team, String employeeId, String firstName, String lastName, LocalDate fromDate) {
        Team updatedTeam = team.assignTeamLeader(employeeId, firstName, lastName, fromDate);
        return updatedTeam;
    }

    public MandatoryDocument createMandatoryDocument(String planId, String coverageId, ProcessType processType, Set<String> documents){
        MandatoryDocument mandatoryDocument;
        if (coverageId!=null)
            mandatoryDocument = MandatoryDocument.createMandatoryDocumentWithCoverageId(new PlanId(planId), new CoverageId(coverageId),processType, documents);
        else
            mandatoryDocument = MandatoryDocument.createMandatoryDocumentWithPlanId(new PlanId(planId), processType, documents);
        return mandatoryDocument;
    }

    public MandatoryDocument updateMandatoryDocument(MandatoryDocument mandatoryDocument, Set<String> documents){
        MandatoryDocument updateMandatoryDocument = mandatoryDocument.updateMandatoryDocument(documents);
        return updateMandatoryDocument;
    }

    public Team inactivateTeam(Team team) {
        Team deactivatedTeam = team.inactivate();
        return deactivatedTeam;
    }
}
