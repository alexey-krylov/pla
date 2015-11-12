package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.pla.core.domain.exception.PlanValidationException;
import com.pla.sharedkernel.domain.model.PolicyTermType;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Getter
public class PlanBuilder {

    private PlanDetail planDetail;
    private PolicyTermType policyTermType;
    private Term policyTerm;
    private PremiumTermType premiumTermType;
    private Term premiumTerm;
    private SumAssured sumAssured;
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();

    public PlanBuilder() {
    }

    public PlanBuilder withPlanDetail(PlanDetail planDetail) {
        this.planDetail = planDetail;
        return this;
    }

    public PlanBuilder withPolicyTerm(PolicyTermType policyTermType, Set<Integer> validValues, Integer maxMaturityAge, Integer groupTerm) {
        this.policyTermType = policyTermType;
        switch (policyTermType) {
            case SPECIFIED_VALUES:
                if (validValues.size() > 0) {
                    this.policyTerm = new Term(validValues, maxMaturityAge);
                } else {
                    this.policyTerm = new Term(groupTerm);
                }
                break;
            case MATURITY_AGE_DEPENDENT:
                this.policyTerm = new Term(validValues);
                break;

        }
        return this;
    }

    public PlanBuilder withPremiumTerm(PremiumTermType paymentTermType,
                                       Set<Integer> validValues, int cutOffAge) {
        this.premiumTermType = paymentTermType;
        switch (paymentTermType) {
            case SPECIFIED_VALUES:
                this.premiumTerm = new Term(validValues, cutOffAge);
                break;
            case SPECIFIED_AGES:
                this.premiumTerm = new Term(validValues);
                break;
            case REGULAR:
                this.premiumTerm = new Term(policyTerm);
                break;
            case SINGLE:
                Set<Integer> premiumTerm = Sets.newLinkedHashSet();
                premiumTerm.add(1);
                this.premiumTerm = new Term(premiumTerm);
                //TODO find out what happens when it is single premium
        }
        return this;
    }

    public PlanBuilder withPlanCoverage(PlanCoverage planCoverage) {
        this.coverages.add(planCoverage);
        return this;
    }

    public PlanBuilder withPlanCoverages(Set<PlanCoverage> planCoverages) {
        this.coverages = Collections.unmodifiableSet(planCoverages);
        return this;
    }

    public PlanBuilder withPlanSumAssured(SumAssuredType sumAssuredType,
                                          BigDecimal minSumAssuredAmount,
                                          BigDecimal maxSumAssuredAmount,
                                          int multiplesOf,
                                          Set<BigDecimal> assuredValues,
                                          BigDecimal incomeMultiplier) {
        SumAssured sumAssured = null;
        switch (sumAssuredType) {
            case RANGE:
                sumAssured = new SumAssured(minSumAssuredAmount,
                        maxSumAssuredAmount, multiplesOf);
                break;
            case SPECIFIED_VALUES:
                Preconditions.checkArgument(UtilValidator.isNotEmpty(assuredValues));
                sumAssured = new SumAssured(new TreeSet(assuredValues));
                break;
            case INCOME_MULTIPLIER:
                sumAssured = SumAssured.createSumAssuredWithIncomeMultiplier(incomeMultiplier);

        }
        checkArgument(sumAssured != null);
        this.sumAssured = sumAssured;
        return this;
    }


    public Plan build(PlanId planId) throws PlanValidationException {
        return new Plan(planId, this);
    }


    public Plan build() throws PlanValidationException {
        return new Plan(new PlanId(), this);
    }
}

