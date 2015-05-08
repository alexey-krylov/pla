package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Samir on 4/22/2015.
 */
public interface IPlanAdapter {

    List<PlanCoverageDetailDto> getPlanAndCoverageDetail(List<PlanId> planIds);

    boolean isValidPlanForRelationship(String planCode, Relationship relationship);

    boolean isValidPlanCoverage(String planCode, String coverageCode);

    boolean isValidPlanSumAssured(String planCode, BigDecimal sumAssured);

    boolean hasPlanContainsIncomeMultiplierSumAssured(String planCode);

    boolean isValidPlanCode(String planCode);
}
