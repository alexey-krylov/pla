package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */

@Document(collection = "PLAN")
@Getter
@ToString(exclude = {"logger"})
@EqualsAndHashCode(exclude = {"logger", "specification"}, callSuper = false)
public class Plan {

    private static final Logger logger = LoggerFactory.getLogger(Plan.class);

    @Id
    private PlanId planId;
    private PlanDetail planDetail;
    private PlanSpecification specification = new PlanSpecification();
    private SumAssuredType sumAssuredType;
    private SumAssured sumAssured;
    private PolicyTermType policyTermType;
    private PremiumTermType premiumTermType;
    private Term premiumTerm;
    private Collection<MaturityAmount> maturityAmounts;
    private Term policyTerm;
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();

    Plan(PlanId planId, PlanBuilder planBuilder) {

        Preconditions.checkArgument(planId != null);
        this.planId = planId;
        Preconditions.checkArgument(planBuilder != null);
        this.planDetail = planBuilder.getPlanDetail();
        this.sumAssured = planBuilder.getSumAssured();
        this.policyTermType = planBuilder.getPolicyTermType();
        this.premiumTermType = planBuilder.getPremiumTermType();
        this.premiumTerm = planBuilder.getPolicyTerm();
        this.policyTerm = planBuilder.getPolicyTerm();
        this.coverages = planBuilder.getCoverages();
        this.maturityAmounts = planBuilder.getMaturityAmounts();

        Map<CoverageId, SumAssured> coverageAssuredMap = planBuilder.getCoverageSumAssuredMap();
        for (CoverageId coverageId : coverageAssuredMap.keySet()) {
            configurePlanCoverageWithSumAssured(coverageId, coverageAssuredMap.get(coverageId));
        }

        Map<CoverageId, Term> coverageTermMap = planBuilder.getCoverageTermMap();
        Map<CoverageId, CoverageTermType> coverageTermTypeMap = planBuilder.getCoverageTermTypeMap();

        for (CoverageId coverageId : coverageTermMap.keySet()) {
            configurePlanCoverageWithTerm(coverageId, coverageTermTypeMap.get(coverageId), coverageTermMap.get(coverageId));
        }
        configureBenefitForPlanCoverage(planBuilder.getPlanCoverageBenefits());
        Preconditions.checkState(specification.isSatisfiedBy(this));
        Preconditions.checkState(specification.checkCoverageTerm(this, coverageTermMap.values()));
    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }

    private void configurePlanCoverageWithSumAssured(CoverageId coverageId, SumAssured sumAssured) {
        if (logger.isTraceEnabled()) {
            logger.trace("configurePlanCoverageWithSumAssured");
        }
        checkArgument(coverageId != null);
        Optional<PlanCoverage> result = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId)).findFirst();
        checkArgument(result.isPresent(), " Cannot find Plan Coverage with the coverage id supplied. %s", coverageId);
        PlanCoverage pc = result.get();
        pc.configureSumAssured(sumAssured);
    }


    private void configurePlanCoverageWithTerm(CoverageId coverageId, CoverageTermType coverageTermType, Term term) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage Term configured.");
        }
        Optional<PlanCoverage> result = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId)).findFirst();
        checkArgument(result.isPresent(), " Cannot find Plan Coverage with the coverage id supplied. %s", coverageId);
        PlanCoverage pc = result.get();
        pc.configureCoverageTerm(coverageTermType, term);
    }

    protected void configureBenefitForPlanCoverage(Set<PlanCoverageBenefit> benefits) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage Sum Assured re-configured");
        }
        PlanCoverage previousPC = null;
        Set<PlanCoverageBenefit> benefitForCoverage = new HashSet<>();
        for (PlanCoverageBenefit each : benefits) {
            CoverageId coverageId = each.getCoverageId();
            Optional<PlanCoverage> result = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId)).findFirst();
            checkArgument(result.isPresent(), " Cannot find Plan Coverage with the coverage id supplied. %s", coverageId);
            PlanCoverage pc = result.get();
            if (previousPC != null && !pc.equals(previousPC)) {
                previousPC.replacePlanCoverageBenefits(new HashSet<>(benefitForCoverage));
                benefitForCoverage.clear();
            }
            benefitForCoverage.add(each);
            previousPC = pc;
        }
        if (benefitForCoverage.size() > 0) {
            previousPC.replacePlanCoverageBenefits(new HashSet(benefitForCoverage));
        }
        benefitForCoverage.clear();
    }


}