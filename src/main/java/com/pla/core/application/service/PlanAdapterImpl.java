package com.pla.core.application.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.SumAssured;
import com.pla.core.query.PlanFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.PlanId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 4/22/2015.
 */

@Service(value = "planAdapter")
public class PlanAdapterImpl implements IPlanAdapter {

    private PlanFinder planFinder;

    @Autowired
    public PlanAdapterImpl(PlanFinder planFinder) {
        this.planFinder = planFinder;
    }

    @Override
    public List<PlanCoverageDetailDto> getPlanAndCoverageDetail(List<PlanId> planIds) {
        List<Plan> plans = planFinder.findPlanBy(planIds);
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = plans.stream().map(new PlanCoverageDetailTransformer()).collect(Collectors.toList());
        return planCoverageDetailDtoList;
    }

    @Override
    public boolean isValidPlanForRelationship(String planCode, Relationship relationship) {
        return false;
    }

    @Override
    public boolean isValidPlanSumAssured(String planCode, BigDecimal sumAssured) {
        return false;
    }

    @Override
    public boolean isValidCoverageSumAssured(String planCode, String coverageCode, BigDecimal sumAssured) {
        return false;
    }

    @Override
    public boolean hasPlanContainsIncomeMultiplierSumAssured(String planCode) {
        return false;
    }

    private class PlanCoverageDetailTransformer implements Function<Plan, PlanCoverageDetailDto> {

        @Override
        public PlanCoverageDetailDto apply(Plan plan) {
            PlanCoverageDetailDto planCoverageDetailDto = new PlanCoverageDetailDto(plan.getPlanId(), plan.getPlanDetail().getPlanName(), plan.getPlanDetail().getPlanCode());
            SumAssured sumAssured = plan.getSumAssured();
            PlanCoverageDetailDto.SumAssuredDto sumAssuredDto = null;
            if (SumAssuredType.RANGE.equals(sumAssured.getSumAssuredType())) {
                sumAssuredDto = planCoverageDetailDto.new SumAssuredDto(sumAssured.getMinSumInsured(), sumAssured.getMaxSumInsured(), sumAssured.getMultiplesOf());
            } else {
                sumAssuredDto = planCoverageDetailDto.new SumAssuredDto(Lists.newArrayList(sumAssured.getSumAssuredValue()));
            }
            planCoverageDetailDto = planCoverageDetailDto.addSumAssured(sumAssuredDto);

            List<String> relations = plan.getPlanDetail().getApplicableRelationships().stream().map(new RelationshipTransformer()).collect(Collectors.toList());
            planCoverageDetailDto = planCoverageDetailDto.addRelationTypes(relations);
            List<PlanCoverageDetailDto.CoverageDto> coverageDtoList = plan.getCoverages().stream().map(new CoverageTransformer(planCoverageDetailDto)).collect(Collectors.toList());
            planCoverageDetailDto = planCoverageDetailDto.addCoverage(coverageDtoList);
            return planCoverageDetailDto;
        }
    }

    private class CoverageTransformer implements Function<PlanCoverage, PlanCoverageDetailDto.CoverageDto> {

        private PlanCoverageDetailDto planCoverageDetailDto;

        CoverageTransformer(PlanCoverageDetailDto planCoverageDetailDto) {
            this.planCoverageDetailDto = planCoverageDetailDto;
        }

        @Override
        public PlanCoverageDetailDto.CoverageDto apply(PlanCoverage coverage) {
            PlanCoverageDetailDto.CoverageDto coverageDto = planCoverageDetailDto.new CoverageDto(coverage.getCoverageCode(), coverage.getCoverageName(), coverage.getCoverageId());
            PlanCoverageDetailDto.SumAssuredDto sumAssuredDto = null;
            SumAssured sumAssured = coverage.getCoverageSumAssured();
            if (SumAssuredType.RANGE.equals(sumAssured.getSumAssuredType())) {
                sumAssuredDto = planCoverageDetailDto.new SumAssuredDto(sumAssured.getMinSumInsured(), sumAssured.getMaxSumInsured(), sumAssured.getMultiplesOf());
            } else {
                sumAssuredDto = planCoverageDetailDto.new SumAssuredDto(Lists.newArrayList(sumAssured.getSumAssuredValue()));
            }
            coverageDto = coverageDto.addSumAssured(sumAssuredDto);
            return coverageDto;
        }
    }

    private class RelationshipTransformer implements Function<Relationship, String> {

        @Override
        public String apply(Relationship relationship) {
            return relationship.description;
        }
    }
}
