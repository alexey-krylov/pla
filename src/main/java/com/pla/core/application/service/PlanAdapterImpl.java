package com.pla.core.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.PlanCoverageBenefit;
import com.pla.core.domain.model.plan.SumAssured;
import com.pla.core.query.BenefitFinder;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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

    private BenefitFinder benefitFinder;

    @Autowired
    public PlanAdapterImpl(PlanFinder planFinder, PlanRepository planRepository, CoverageFinder coverageFinder, BenefitFinder benefitFinder) {
        this.planFinder = planFinder;
        this.planRepository = planRepository;
        this.coverageFinder = coverageFinder;
        this.benefitFinder = benefitFinder;
    }

    @Override
    public List<PlanCoverageDetailDto> getPlanAndCoverageDetail(List<PlanId> planIds) {
        List<Plan> plans = planFinder.findPlanBy(planIds);
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = plans.stream().map(new PlanCoverageDetailTransformer()).collect(Collectors.toList());
        return planCoverageDetailDtoList;
    }

    @Override
    public List<PlanCoverageDetailDto> getAllPlanAndCoverageDetail() {
        List<Map<String, Object>> plans = planFinder.getAllPlans();
        if (isEmpty(plans)) {
            return Collections.EMPTY_LIST;
        }
        List<PlanCoverageDetailDto> planDetail = Lists.newArrayList();
        plans.forEach(planMap -> {
            PlanCoverageDetailDto planCoverageDetailDto = new PlanCoverageDetailDto(new PlanId(planMap.get("planId").toString()), planMap.get("planName").toString(), planMap.get("planCode").toString());
            planCoverageDetailDto.addCoverage(Lists.newArrayList());
            planCoverageDetailDto.addSumAssured(planCoverageDetailDto.new SumAssuredDto(Lists.newArrayList()));
            planCoverageDetailDto.addRelationTypes(Lists.newArrayList());
            planDetail.add(planCoverageDetailDto);
        });
        return planDetail;
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
        Map<String, Object> coverageMap = coverageFinder.getCoverageDetailByCode(coverageCode);
        String coverageId = (String) coverageMap.get("coverageId");
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidCoverage(new CoverageId(coverageId));
    }

    @Override
    public boolean isValidPlanCoverageSumAssured(String planCode, String coverageCode, BigDecimal sumAssured) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Map<String, Object> coverageMap = coverageFinder.getCoverageDetailByCode(coverageCode);
        String coverageId = (String) coverageMap.get("coverageId");
        Plan plan = plans.get(0);
        return plan.isValidCoverageSumAssured(sumAssured, new CoverageId(coverageId));
    }

    @Override
    public boolean isValidPlanCoverageBenefit(String planCode, String coverageCode, String benefitCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        Map<String, Object> coverageMap = coverageFinder.getCoverageDetailByCode(coverageCode);
        String coverageId = (String) coverageMap.get("coverageId");
        Map<String, Object> benefitMap = benefitFinder.findBenefitByCode(benefitCode);
        String benefitId = benefitMap.get("benefitId") != null ? (String) benefitMap.get("benefitId") : "";
        return plan.isValidPlanCoverageBenefit(new CoverageId(coverageId), new BenefitId(benefitId));
    }

    @Override
    public boolean isValidPlanCoverageBenefitLimit(String planCode, String coverageCode, String benefitCode, BigDecimal benefitLimit) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        Map<String, Object> coverageMap = coverageFinder.getCoverageDetailByCode(coverageCode);
        String coverageId = (String) coverageMap.get("coverageId");
        Map<String, Object> benefitMap = benefitFinder.findBenefitByCode(benefitCode);
        String benefitId = benefitMap.get("benefitId") != null ? (String) benefitMap.get("benefitId") : "";
        return plan.isValidPlanCoverageBenefitLimit(new CoverageId(coverageId), new BenefitId(benefitId), benefitLimit);
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
    public boolean isValidUnderWriterPlanSumAssured(String planCode, BigDecimal sumAssured) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidUnderWriterSumAssured(sumAssured);
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
    public boolean isValidCoverageAgeForGivenCoverageCode(String planCode, String coverageCode, int age) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        Map coverage = coverageFinder.getCoverageDetailByCode(coverageCode);
        return plan.isValidCoverageAge(age, new CoverageId(coverage.get("coverageId").toString()));
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
    public BigDecimal getIncomeMultiplier(String planCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isNotEmpty(plans)) {
            Plan plan = plans.get(0);
            if (plan.hasPlanContainsSumAssuredTypeAsIncomeMultiplier()) {
                return plan.getSumAssured().getIncomeMultiplier();
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean isValidPlanCode(String planCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        List<Plan> activePlans = plans.stream().filter(plan -> (plan.getPlanDetail().getWithdrawalDate() == null || DateTime.now().isBefore(plan.getPlanDetail().getWithdrawalDate()))).collect(Collectors.toList());
        return isNotEmpty(activePlans);
    }

    @Override
    public boolean isValidPlanCodeForBusinessLine(String planCode, LineOfBusinessEnum lineOfBusinessEnum) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return lineOfBusinessEnum.equals(plan.getPlanDetail().getLineOfBusinessId());
    }

    @Override
    public PlanId getPlanId(String planCode) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        return isNotEmpty(plans) ? plans.get(0).getIdentifier() : null;
    }

    /*
    *
    * returns true if any plan is active and
     *  false if plan is not active
    * */
    @Override
    public boolean isPlanActive(String planCode) {
        int activePlanCount = planFinder.findActivePlanByPlanCode(planCode);
        return activePlanCount != 0;
    }

    /*
    * @TODO please review @Samir
    * */
     @Override
    public Set<String> getConfiguredEndorsementType(Set<PlanId> planIds) {
        Set<String> endorsementType =  planFinder.findConfiguredEndorsementType(planIds);
        return endorsementType;
    }

    @Override
    public boolean isValidAgeRange(String planCode, int age) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        if (isEmpty(plans)) {
            return false;
        }
        Plan plan = plans.get(0);
        return plan.isValidAgeRange(age);
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
            Set<PlanCoverageDetailDto.BenefitDto> benefitDtoList = isNotEmpty(coverage.getPlanCoverageBenefits()) ? coverage.getPlanCoverageBenefits().stream().map(new Function<PlanCoverageBenefit, PlanCoverageDetailDto.BenefitDto>() {
                @Override
                public PlanCoverageDetailDto.BenefitDto apply(PlanCoverageBenefit planCoverageBenefit) {
                    Map<String, Object> benefitMap = benefitFinder.findBenefitById(planCoverageBenefit.getBenefitId().getBenefitId());
                    PlanCoverageDetailDto.BenefitDto benefitDto = planCoverageDetailDto.new BenefitDto(planCoverageBenefit.getBenefitId(), (String) benefitMap.get("benefitName"), (String) benefitMap.get("benefitCode"), planCoverageBenefit.getBenefitLimit());
                    return benefitDto;
                }
            }).collect(Collectors.toSet()) : Sets.newHashSet();
            if (isNotEmpty(benefitDtoList)) {
                coverageDto.addAllBenefitDetail(benefitDtoList);
            }
            return coverageDto;
        }
    }

    private class RelationshipTransformer implements Function<Relationship, String> {

        @Override
        public String apply(Relationship relationship) {
            return relationship.description;
        }
    }

    public Plan getPlanByPlanId(PlanId planId){
        return planRepository.findOne(planId);
    }

    @Override
    public String getPlanCodeById(PlanId planId) {
        Plan plan = planRepository.findOne(planId);
        return plan!=null?plan.getPlanDetail().getPlanCode():"";
    }

}
