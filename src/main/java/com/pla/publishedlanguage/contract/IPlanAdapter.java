package com.pla.publishedlanguage.contract;

import com.pla.core.domain.model.plan.Plan;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by Samir on 4/22/2015.
 */
public interface IPlanAdapter {

    List<PlanCoverageDetailDto> getPlanAndCoverageDetail(List<PlanId> planIds);

    List<PlanCoverageDetailDto> getAllPlanAndCoverageDetail();

    Plan getPlanByPlanId(PlanId planId);

    boolean isValidPlanForRelationship(String planCode, Relationship relationship);

    boolean isValidPlanCoverage(String planCode, String coverageCode);

    boolean isValidPlanCoverageSumAssured(String planCode, String coverageCode, BigDecimal sumAssured);

    boolean isValidPlanCoverageBenefit(String planCode, String coverageCode, String benefitCode);

    boolean isValidPlanCoverageBenefitLimit(String planCode, String coverageCode, String benefitCode, BigDecimal benefitLimit);

    boolean isValidPlanSumAssured(String planCode, BigDecimal sumAssured);

    boolean isValidUnderWriterPlanSumAssured(String planCode, BigDecimal sumAssured);

    boolean isValidCoverageSumAssured(String planCode, String coverageId, BigDecimal sumAssured);

    boolean isValidPlanAge(String planCode, int age);

    boolean isValidAgeRange(String planCode, int age);

    boolean isValidCoverageAge(String planCode, String coverageId, int age);

    boolean isValidCoverageAgeForGivenCoverageCode(String planCode, String coverageCode, int age);

    boolean hasPlanContainsIncomeMultiplierSumAssured(String planCode);

    boolean isValidPlanCode(String planCode);

    boolean isValidPlanCodeForBusinessLine(String planCode,LineOfBusinessEnum lineOfBusinessEnum);

    PlanId getPlanId(String planCode);

    boolean isPlanActive(String planCode);

    Set<String> getConfiguredEndorsementType(Set<PlanId> planIds);



}

