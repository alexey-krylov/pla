/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.exception.TeamDomainException;
import com.pla.core.domain.model.generalinformation.*;
import com.pla.core.dto.GeneralInformationDto;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import com.pla.sharedkernel.domain.model.Tax;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

    public Benefit inactivateBenefit(Benefit benefit,boolean isBenefitUpdatable) {
        if (!isBenefitUpdatable) {
            throw new BenefitDomainException("Benefit is associated with active coverage");
        }
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

    public Coverage updateCoverage(Coverage coverage, String newCoverageName, String description,Set<Benefit> benefits, boolean isCoverageNameUnique) {
        if (!isCoverageNameUnique && !coverage.getCoverageName().equals(new CoverageName(newCoverageName))) {
            throw new CoverageException("Coverage name already satisfied");
        }
        return coverage.updateCoverageName(newCoverageName).updateBenefit(benefits).updateDescription(description);
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

    public ProductLineGeneralInformation createProductLineGeneralInformation(LineOfBusinessId lineOfBusinessId,GeneralInformationDto generalInformationDto){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(lineOfBusinessId);
        productLineGeneralInformation =  assignProductLineProcess(generalInformationDto, productLineGeneralInformation);
        return productLineGeneralInformation;
    }

    public OrganizationGeneralInformation createOrganizationGeneralInformation(List<Map<ModalFactorItem, BigDecimal>> modalFactorItems, List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems,Map<Tax, BigDecimal> serviceTax){
        String organizationInformationId = new ObjectId().toString();
        OrganizationGeneralInformation organizationGeneralInformation = OrganizationGeneralInformation.createOrganizationGeneralInformation(organizationInformationId);
        organizationGeneralInformation.withDiscountFactorOrganizationInformation(discountFactorItems);
        organizationGeneralInformation.withModalFactorOrganizationInformation(modalFactorItems);
        organizationGeneralInformation.withServiceTaxOrganizationInformation(serviceTax);
        return organizationGeneralInformation;
    }

    public OrganizationGeneralInformation updateOrganizationInformation(OrganizationGeneralInformation organizationGeneralInformation,GeneralInformationDto generalInformationDto){
        organizationGeneralInformation.withDiscountFactorOrganizationInformation(generalInformationDto.getDiscountFactorItems());
        organizationGeneralInformation.withModalFactorOrganizationInformation(generalInformationDto.getModelFactorItems());
        organizationGeneralInformation.withServiceTaxOrganizationInformation(generalInformationDto.getServiceTax());
        return organizationGeneralInformation;
    }

    public ProductLineGeneralInformation updateProductLineInformation(ProductLineGeneralInformation productLineGeneralInformation,GeneralInformationDto generalInformationDto){
        productLineGeneralInformation = assignProductLineProcess(generalInformationDto, productLineGeneralInformation);
        return productLineGeneralInformation;
    }

    private ProductLineGeneralInformation assignProductLineProcess(GeneralInformationDto generalInformationDto, ProductLineGeneralInformation productLineGeneralInformation) {
        productLineGeneralInformation.withQuotationProcessInformation(generalInformationDto.getQuotationProcessItems());
        productLineGeneralInformation.withEnrollmentProcessGeneralInformation(generalInformationDto.getEnrollmentProcessItems());
        productLineGeneralInformation.withReinstatementProcessInformation(generalInformationDto.getReinstatementProcessItems());
        productLineGeneralInformation.withEndorsementProcessInformation(generalInformationDto.getEndorsementProcessItems());
        productLineGeneralInformation.withClaimProcessInformation(generalInformationDto.getClaimProcessItems());
        productLineGeneralInformation.withPolicyFeeProcessInformation(generalInformationDto.getPolicyFeeProcessItems());
        productLineGeneralInformation.withPolicyProcessMinimumLimit(generalInformationDto.getPolicyProcessMinimumLimitItems());
        productLineGeneralInformation.withSurrenderProcessInformation(generalInformationDto.getSurrenderProcessItems());
        productLineGeneralInformation.withMaturityProcessInformation(generalInformationDto.getMaturityProcessItems());
        return productLineGeneralInformation;
    }
}
