/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.exception.TeamDomainException;
import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.core.domain.model.plan.commission.Commission;
import com.pla.core.domain.model.plan.commission.CommissionTerm;
import com.pla.core.dto.PolicyProcessMinimumLimitItemDto;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.*;
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
            throw new BenefitDomainException("Benefit already described");
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

    public Coverage createCoverage(boolean isCodeAndNameIsUnique,String coverageId, String coverageName,String coverageCode,String description,  Set<Benefit> benefits) {
        if (!isCodeAndNameIsUnique)
            throw new CoverageException("Coverage already described");
        return new Coverage(new CoverageId(coverageId), new CoverageName(coverageName),coverageCode, benefits, CoverageStatus.ACTIVE).updateDescription(description);
    }

    public Coverage updateCoverage(Coverage coverage, String newCoverageName,String newCoverageCode, String description,Set<Benefit> benefits, boolean isCodeAndNameIsUnique) {
        if (!isCodeAndNameIsUnique)
            throw new CoverageException("Coverage already described");
        return coverage.updateCoverageName(newCoverageName).updateCoverageCode(newCoverageCode).updateBenefit(benefits).updateDescription(description);
    }

    public Coverage inactivateCoverage(Coverage coverage) {
        Coverage deactivatedCoverage = coverage.deactivate();
        return deactivatedCoverage;
    }


    public Team createTeam(boolean isTeamUnique, String teamId, String teamName, String teamCode, String regionCode, String branchCode
            , String employeeId, LocalDate fromDate, String firstName, String lastName) {
        if (!isTeamUnique) {
            throw new TeamDomainException("Team name or Team Code already satisfied");
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

    public Team inactivateTeam(Team team, boolean isTeamAssociatedWithAgent) {
        if (isTeamAssociatedWithAgent) {
            throw new TeamDomainException("Team is associated with Agent!!");
        }
        Team deactivatedTeam = team.inactivate();
        return deactivatedTeam;
    }

    public ProductLineGeneralInformation createProductLineGeneralInformation(LineOfBusinessId lineOfBusinessId, List<Map<ProductLineProcessType,Integer>> quotationProcessItem,List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem ,
                                                                             List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem ,List<Map<ProductLineProcessType,Integer>> endorsementProcessItem,
                                                                             List<Map<ProductLineProcessType,Integer>> claimProcessItem, List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess,List<PolicyProcessMinimumLimitItemDto>   minimumLimitProcess,
                                                                             List<Map<ProductLineProcessType,Integer>> surrenderProcessItem ,List<Map<ProductLineProcessType,Integer>> maturityProcessItem){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(lineOfBusinessId);
        productLineGeneralInformation =  assignProductLineProcess(quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem, productLineGeneralInformation);
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

    public OrganizationGeneralInformation updateOrganizationInformation(OrganizationGeneralInformation organizationGeneralInformation, List<Map<ModalFactorItem, BigDecimal>>  modalFactorItem ,List<Map<DiscountFactorItem, BigDecimal>>  discountFactorItem,Map<Tax,BigDecimal> serviceTax){
        organizationGeneralInformation.withDiscountFactorOrganizationInformation(discountFactorItem);
        organizationGeneralInformation.withModalFactorOrganizationInformation(modalFactorItem);
        organizationGeneralInformation.withServiceTaxOrganizationInformation(serviceTax);
        return organizationGeneralInformation;
    }

    public ProductLineGeneralInformation updateProductLineInformation(ProductLineGeneralInformation productLineGeneralInformation,List<Map<ProductLineProcessType,Integer>> quotationProcessItem,List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem ,
                                                                      List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem ,List<Map<ProductLineProcessType,Integer>> endorsementProcessItem,
                                                                      List<Map<ProductLineProcessType,Integer>> claimProcessItem, List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess,List<PolicyProcessMinimumLimitItemDto>   minimumLimitProcess,
                                                                      List<Map<ProductLineProcessType,Integer>> surrenderProcessItem ,List<Map<ProductLineProcessType,Integer>> maturityProcessItem){
        productLineGeneralInformation = assignProductLineProcess(quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem, productLineGeneralInformation);
        return productLineGeneralInformation;
    }

    private ProductLineGeneralInformation assignProductLineProcess( List<Map<ProductLineProcessType,Integer>> quotationProcessItem,List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem ,
                                                                    List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem ,List<Map<ProductLineProcessType,Integer>> endorsementProcessItem,
                                                                    List<Map<ProductLineProcessType,Integer>> claimProcessItem, List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess,List<PolicyProcessMinimumLimitItemDto>   minimumLimitProcess,
                                                                    List<Map<ProductLineProcessType,Integer>> surrenderProcessItem ,List<Map<ProductLineProcessType,Integer>> maturityProcessItem, ProductLineGeneralInformation productLineGeneralInformation) {
        productLineGeneralInformation.withQuotationProcessInformation(quotationProcessItem);
        productLineGeneralInformation.withEnrollmentProcessGeneralInformation(enrollmentProcessItem);
        productLineGeneralInformation.withReinstatementProcessInformation(reinstatementProcessItem);
        productLineGeneralInformation.withEndorsementProcessInformation(endorsementProcessItem);
        productLineGeneralInformation.withClaimProcessInformation(claimProcessItem);
        productLineGeneralInformation.withPolicyFeeProcessInformation(policyFeeProcess);
        productLineGeneralInformation.withPolicyProcessMinimumLimit(minimumLimitProcess);
        productLineGeneralInformation.withSurrenderProcessInformation(surrenderProcessItem);
        productLineGeneralInformation.withMaturityProcessInformation(maturityProcessItem);
        return productLineGeneralInformation;
    }

    public Commission createCommission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, PremiumFee premiumFee, LocalDate fromDate) {

        return Commission.createCommission(commissionId, planId, availableFor, commissionType, premiumFee, fromDate);
    }

    public Commission updateCommissionTerm(Commission commission, Set<CommissionTerm> commissionSet, List<Integer> policyTerms) {

        return commission.updateWithCommissionTerms(commissionSet, policyTerms);
    }


}
