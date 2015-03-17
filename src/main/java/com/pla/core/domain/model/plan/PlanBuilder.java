package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 14/03/2015
 */
public class PlanBuilder {

    PlanId planId;
    PlanDetail planDetail;
    SumAssured sumAssured;
    PolicyTerm policyTerm;
    PlanPayment planPayment;
    Set<PlanCoverage> coverages;
    Set<MaturityAmount> maturityAmountSet = new HashSet<MaturityAmount>();

    public PlanBuilder withPlanId(@NotNull PlanId planId) {
        this.planId = planId;
        return this;
    }

    public PlanBuilder withPlanDetail(@NotNull PlanDetail planDetail) {
        this.planDetail = planDetail;
        return this;
    }


    public PlanBuilder withSumAssuredByRange(@NotNull BigDecimal minSumInsured, @NotNull BigDecimal maxSumInsured, @NotNull int multiplesOf) {
        this.sumAssured = new SumAssuredByRange(minSumInsured, maxSumInsured, multiplesOf);
        return this;
    }

    public PlanBuilder withSumAssuredBasedOnValue(Set<BigDecimal> sumInsuredValues) {
        this.sumAssured = new SumAssured(sumInsuredValues);
        return this;
    }


    public PlanBuilder withPolicyTermBasedOnAge(int maxMaturityAge) {
        this.policyTerm = new PolicyTerm(maxMaturityAge);
        return this;
    }

    public PlanBuilder withPolicyTermBasedOnValue(Set<Integer> validTerms, int maxMaturityAge) {
        this.policyTerm = new PolicyTerm(validTerms, maxMaturityAge);
        return this;
    }

    public PlanBuilder withPaymentTermBasedOnAge(int paymentCutOffAge) {
        this.planPayment = new PlanPayment(new PremiumPayment(paymentCutOffAge));
        return this;
    }

    public PlanBuilder withPaymentTermBasedOnValue(Set<Integer> validTerms) {
        this.planPayment = new PlanPayment(new PremiumPayment(validTerms));
        return this;
    }

    public PlanBuilder withMaturityAmount(int maturityYear, BigDecimal guaranteedSurvivalBenefitAmount) {
        this.maturityAmountSet.add(new MaturityAmount(maturityYear, guaranteedSurvivalBenefitAmount));
        return this;
    }

    public PlanBuilder withCoverages(Set<PlanCoverage> coverages) {
        this.coverages = coverages;
        return this;
    }

    public PlanBuilder withCoverageBenefit(CoverageId coverageId, PlanCoverageBenefit planCoverageBenefit) {
        PlanCoverage planCoverage = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId))
                .findFirst().get();
        planCoverage.addCoverageBenefit(coverageId, planCoverageBenefit);
        return this;
    }

    public Plan build() {
        return new Plan(this);
    }
}
