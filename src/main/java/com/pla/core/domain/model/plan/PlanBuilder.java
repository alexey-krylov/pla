package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.BenefitId;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Getter
public class PlanBuilder {

    private Set<MaturityAmount> maturityAmounts = new HashSet<>();
    private PlanDetail planDetail;
    private PolicyTermType policyTermType;
    private Term policyTerm;
    private PremiumTermType premiumTermType;
    private Term premiumTerm;
    private SumAssured sumAssured;
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();
    private Map<CoverageId, SumAssured> coverageSumAssuredMap = new HashMap<>();
    private Map<CoverageId, Term> coverageTermMap = new HashMap<>();
    private Map<CoverageId, CoverageTermType> coverageTermTypeMap = new HashMap<>();
    private Set<PlanCoverageBenefit> planCoverageBenefits;

    public PlanBuilder() {
        this.planCoverageBenefits = new TreeSet(new Comparator<PlanCoverageBenefit>() {
            @Override
            public int compare(PlanCoverageBenefit o1, PlanCoverageBenefit o2) {
                return o1.getBenefitLimit().compareTo(o2.getBenefitLimit());
            }
        });
    }

    public PlanBuilder withPlanDetail(PlanDetail planDetail) {
        this.planDetail = planDetail;
        return this;
    }

    public PlanBuilder withMaturityAmount(int maturityYear, BigDecimal guaranteedSurvivalBenefitAmount) {
        this.maturityAmounts.add(new MaturityAmount(maturityYear, guaranteedSurvivalBenefitAmount));
        return this;
    }

    public PlanBuilder withPlanCoverageBenefit(CoverageId coverageId,
                                               BenefitId benefitId, CoverageBenefitDefinition definedPer,
                                               CoverageBenefitType coverageBenefitType,
                                               BigDecimal benefitLimit, BigDecimal maxLimit) {
        this.planCoverageBenefits.add(new PlanCoverageBenefit(coverageId, benefitId, definedPer,
                coverageBenefitType, benefitLimit, maxLimit));
        return this;
    }

    public PlanBuilder withPolicyTerm(PolicyTermType policyTermType, Set<Integer> validValues, int maxMaturityAge) {
        this.policyTermType = policyTermType;
        switch (policyTermType) {
            case SPECIFIED_VALUES:
                this.policyTerm = new Term(validValues, maxMaturityAge);
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

    public PlanBuilder withMaturityAmounts(Set<MaturityAmount> maturityAmounts) {
        this.maturityAmounts = Collections.unmodifiableSet(maturityAmounts);
        return this;
    }

    public PlanBuilder withPlanSumAssured(SumAssuredType sumAssuredType,
                                          BigDecimal minSumAssuredAmount,
                                          BigDecimal maxSumAssuredAmount,
                                          int multiplesOf,
                                          Set<BigDecimal> assuredValues,
                                          int percentage) {
        SumAssured sumAssured = null;
        switch (sumAssuredType) {
            case RANGE:
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MinSumAssuredAmount greater than zero Expected, but got %d", minSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MaxSumAssuredAmount greater than zero Expected, but got %d", maxSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(minSumAssuredAmount) == 1,
                        "MaxSumAssuredAmount>MinSumAssuredAmount Expected, but %d>%d",
                        maxSumAssuredAmount, minSumAssuredAmount);
                Preconditions.checkArgument(multiplesOf % 10 == 0, " Not valid Multiples.");
                sumAssured = new SumAssured(minSumAssuredAmount,
                        maxSumAssuredAmount, multiplesOf);
                break;
            case SPECIFIED_VALUES:
                Preconditions.checkArgument(UtilValidator.isNotEmpty(assuredValues));
                sumAssured = new SumAssured(new TreeSet(assuredValues));
                break;
        }
        checkArgument(sumAssured != null);
        this.sumAssured = sumAssured;
        return this;
    }

    public PlanBuilder withSumAssuredForPlanCoverage(CoverageId coverageId,
                                                     SumAssuredType sumAssuredType,
                                                     BigDecimal minSumAssuredAmount,
                                                     BigDecimal maxSumAssuredAmount,
                                                     int multiplesOf,
                                                     Set<BigDecimal> assuredValues,
                                                     int percentage) {
        switch (sumAssuredType) {
            case SPECIFIED_VALUES:
                Preconditions.checkArgument(UtilValidator.isNotEmpty(assuredValues));
                sumAssured = new SumAssured(new TreeSet<>(assuredValues));
                break;
            case DERIVED:
                Preconditions.checkArgument(percentage > 0);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1);
                Preconditions.checkArgument(coverageId != null);
                sumAssured = new SumAssured(coverageId, percentage, BigInteger.valueOf(maxSumAssuredAmount.longValue()));
                break;
            case RANGE:
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MinSumAssuredAmount greater than zero Expected, but got %d", minSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MaxSumAssuredAmount greater than zero Expected, but got %d", maxSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(minSumAssuredAmount) == 1,
                        "MaxSumAssuredAmount>MinSumAssuredAmount Expected, but %d>%d",
                        maxSumAssuredAmount, minSumAssuredAmount);
                Preconditions.checkArgument(multiplesOf % 10 == 0, " Not valid Multiples.");
                sumAssured = new SumAssured(minSumAssuredAmount, maxSumAssuredAmount, multiplesOf);
                break;
        }
        coverageSumAssuredMap.put(coverageId, sumAssured);
        return this;
    }

    public PlanBuilder withTermForPlanCoverage(CoverageId coverageId, CoverageTermType coverageTermType, Set<Integer> validTerms, int maxMaturityAge) {

        Term coverageTerm = null;
        switch (coverageTermType) {
            case SPECIFIED_VALUES:
                checkArgument(maxMaturityAge > 0);
                checkArgument(UtilValidator.isNotEmpty(validTerms));
                coverageTerm = new Term(validTerms, maxMaturityAge);
                break;
            case AGE_DEPENDENT:
                checkArgument(UtilValidator.isNotEmpty(validTerms));
                coverageTerm = new Term(validTerms);
                break;
            case POLICY_TERM:
                checkState(policyTerm != null, "Policy Term is not configured.");
                coverageTerm = new Term(policyTerm);
                break;
        }
        coverageTermTypeMap.put(coverageId, coverageTermType);
        coverageTermMap.put(coverageId, coverageTerm);
        return this;
    }

    public PlanBuilder withCoverageBenefits(Set<PlanCoverageBenefit> benefits) {

        benefits.forEach(benefit -> this.planCoverageBenefits.add(benefit));
        return this;
    }

    Plan build(PlanId planId) {
        return new Plan(planId, this);
    }
}

