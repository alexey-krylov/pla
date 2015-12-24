package com.pla.core.SBCM.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.query.SBCMFinder;
import com.pla.core.SBCM.repository.SBCMRepository;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.PlanCoverageBenefit;
import com.pla.core.repository.PlanRepository;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@DomainService
public class SBCMService {
    private PlanRepository planRepository;
    private SBCMFinder sbcmFinder;
    private SBCMRepository sbcmRepository;

    @Autowired
    public SBCMService(PlanRepository planRepository, SBCMFinder sbcmFinder, SBCMRepository sbcmRepository){
        this.planRepository = planRepository;
        this.sbcmRepository = sbcmRepository;
        this.sbcmFinder = sbcmFinder;
    }

    public List<Map<String, Object>> getAllPlanWithCoverageAndBenefits(){
        List<Plan> plans = planRepository.findAll();
        return isNotEmpty(plans) ? plans.stream().map(new Function<Plan, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Plan plan) {
                return constructOptimizedDetailsMapFromPlan(plan);
            }
        }).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    private Map<String, Object> constructOptimizedDetailsMapFromPlan(Plan plan) {
        Map<String, Object> planMap = Maps.newLinkedHashMap();
        if(isNotEmpty(plan.getCoverages())){
            planMap.put("planCode", plan.getPlanDetail().getPlanCode());
            planMap.put("planName", plan.getPlanDetail().getPlanName());
            planMap.put("coverages", getCoveragesFromPlan(plan));
        }
        return planMap;
    }

    private List<Map<String, Object>> getCoveragesFromPlan(Plan plan) {
        return isNotEmpty(plan.getCoverages()) ? plan.getCoverages().stream().map(new Function<PlanCoverage, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(PlanCoverage planCoverage) {
                return constructCoveragesAndRelatedBenefitsFromPlan(planCoverage);
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private Map<String, Object> constructCoveragesAndRelatedBenefitsFromPlan(PlanCoverage planCoverage) {
        Map<String, Object> coverageMap = Maps.newLinkedHashMap();
        coverageMap.put("coverageId", planCoverage.getCoverageId());
        coverageMap.put("coverageName", getCoverageName(planCoverage));
        coverageMap.put("benefits", getBenefitsFromGivenCoverage(planCoverage));
        return coverageMap;
    }

    private String getCoverageName(PlanCoverage planCoverage) {
        Map<String, Object> coverage = sbcmFinder.getCoverageDetail(planCoverage.getCoverageId().toString());
        return coverage.get("coverageName").toString();
    }

    private Object getBenefitsFromGivenCoverage(PlanCoverage planCoverage) {
        return isNotEmpty(planCoverage.getPlanCoverageBenefits()) ? planCoverage.getPlanCoverageBenefits().stream().map(new Function<PlanCoverageBenefit, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(PlanCoverageBenefit planCoverageBenefit) {
                return constructBenefitsFromGivenCoverage(planCoverageBenefit);
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private Map<String, Object> constructBenefitsFromGivenCoverage(PlanCoverageBenefit planCoverageBenefit) {
        Map<String, Object> benefitMap = Maps.newLinkedHashMap();
        benefitMap.put("benefitId", planCoverageBenefit.getBenefitId());
        benefitMap.put("benefitName", planCoverageBenefit.getBenefitName());
        return benefitMap;
    }
}
