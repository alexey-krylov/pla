package com.pla.core.application.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.SumAssured;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/22/2015.
 */

@Service(value = "planAdapter")
public class PlanAdapterImpl implements IPlanAdapter {

    private PlanFinder planFinder;

    private PlanRepository planRepository;

    private CoverageFinder coverageFinder;

    @Autowired
    public PlanAdapterImpl(PlanFinder planFinder, PlanRepository planRepository, CoverageFinder coverageFinder) {
        this.planFinder = planFinder;
        this.planRepository = planRepository;
        this.coverageFinder = coverageFinder;
    }

    @Override
    public List<PlanCoverageDetailDto> getPlanAndCoverageDetail(List<PlanId> planIds) {
        List<Plan> plans = planFinder.findPlanBy(planIds);
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = plans.stream().map(new PlanCoverageDetailTransformer()).collect(Collectors.toList());
        return planCoverageDetailDtoList;
    }

    //TODO: Check with Samir why is this required.
    @Override
    public List<PlanCoverageDetailDto> getAllPlanAndCoverageDetail() {
      /*  List<Plan> plans = planFinder.findAllPlanForThymeleaf();
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = plans.stream().map(new PlanCoverageDetailTransformer()).collect(Collectors.toList());*/
        return null;
    }

    @Override
    public boolean isValidPlanForRelationship(String planCode, Relationship relationship) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isPlanApplicableForRelationship(relationship);
    }

    @Override
    public boolean isValidPlanCoverage(String planCode, String coverageCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        boolean isCoverageExist = plan.getCoverages().stream().filter(new Predicate<PlanCoverage>() {
            @Override
            public boolean test(PlanCoverage planCoverage) {
                return coverageCode.equals(planCoverage.getCoverageCode());
            }
        }).findAny().isPresent();
        return isCoverageExist;
    }

    @Override
    public boolean isValidPlanSumAssured(String planCode, BigDecimal sumAssured) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidSumAssured(sumAssured);
    }

    @Override
    public boolean isValidCoverageSumAssured(String planCode, String coverageId, BigDecimal sumAssured) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidCoverageSumAssured(sumAssured, new CoverageId(coverageId));
    }

    @Override
    public boolean isValidPlanAge(String planCode, int age) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidAge(age);
    }

    @Override
    public boolean isValidCoverageAge(String planCode, String coverageId, int age) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidCoverageAge(age, new CoverageId(coverageId));
    }

    @Override
    public boolean hasPlanContainsIncomeMultiplierSumAssured(String planCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.hasPlanContainsSumAssuredTypeAsIncomeMultiplier();
    }

    @Override
    public boolean isValidPlanCode(String planCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        return isNotEmpty(plans);
    }

    @Override
    public PlanId getPlanId(String planCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        return isNotEmpty(plans) ? plans.get(0).getIdentifier() : null;
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
            List<PlanCoverageDetailDto.CoverageDto> coverageDtoList = plan.getOptionalCoverages().stream().map(new CoverageTransformer(planCoverageDetailDto)).collect(Collectors.toList());
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
            Map<String, Object> coverageMap = coverageFinder.getCoverageDetail(coverage.getCoverageId().getCoverageId());
            String coverageCode = "";
            String coverageName = "";
            if (coverageMap != null) {
                coverageCode = (String) coverageMap.get("coverageCode");
                coverageName = (String) coverageMap.get("coverageName");
            }
            PlanCoverageDetailDto.CoverageDto coverageDto = planCoverageDetailDto.new CoverageDto(coverageCode, coverageName, coverage.getCoverageId());
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
