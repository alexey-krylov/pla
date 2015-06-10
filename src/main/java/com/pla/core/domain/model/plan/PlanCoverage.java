package com.pla.core.domain.model.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@EqualsAndHashCode(of = {"coverageId"})
@ToString()
@Getter
public class PlanCoverage {

    private CoverageId coverageId;
    private String coverageCode;
    private String coverageName;
    private CoverageCover coverageCover;
    private CoverageType coverageType;
    private String deductibleType;
    private BigDecimal deductibleAmount;
    private int waitingPeriod;
    private int minAge;
    private int maxAge;
    private Boolean taxApplicable;
    private SumAssured coverageSumAssured;
    private Term coverageTerm;
    private CoverageTermType coverageTermType;
    private Set<MaturityAmount> maturityAmounts = new HashSet<>();
    /**
     * Holds the Benefits that are applicable for Plan.
     */
    private Set<PlanCoverageBenefit> planCoverageBenefits = new HashSet<PlanCoverageBenefit>();

    PlanCoverage() {

    }

    PlanCoverage(PlanCoverageBuilder builder) {
        checkArgument(builder.coverageId != null, "Coverage is mandatory.");
        this.coverageId = builder.coverageId;
        this.coverageCode = builder.coverageCode;
        this.coverageName = builder.coverageName;
        checkArgument(builder.coverageCover != null, "coverageCover !=null Expected, but %s !=null ", builder.coverageCover);
        this.coverageCover = builder.coverageCover;

        checkArgument(builder.coverageType != null, "coverageType !=null Expected, but %s !=null ", builder.coverageType);
        this.coverageType = builder.coverageType;

        if (builder.deductibleAmount != null) {
            checkArgument(BigDecimal.ZERO.compareTo(builder.deductibleAmount) == -1, "Deductible Amount has to be greater than 0");
            this.deductibleAmount = builder.deductibleAmount;
        }

        this.waitingPeriod = builder.waitingPeriod;
        checkArgument(builder.minAge > 0, "Min Age for this coverage is missing.");
        this.minAge = builder.minAge;

        checkArgument(builder.maxAge > 0, "Max Entry Age for this coverage is missing.");
        this.maxAge = builder.maxAge;

        checkArgument(builder.taxApplicable != null, "Tax Applicable is mandatory.");
        this.taxApplicable = builder.taxApplicable;
        planCoverageBenefits = new HashSet<PlanCoverageBenefit>();
        this.coverageTerm = builder.coverageTerm;
        this.deductibleType = builder.deductibleType;
        this.coverageSumAssured = builder.sumAssured;
        this.maturityAmounts = builder.maturityAmounts;
        this.coverageTermType = builder.coverageTermType;
        this.planCoverageBenefits = builder.planCoverageBenefits;
    }

    public static PlanCoverageBuilder builder() {
        return new PlanCoverageBuilder();
    }

    public void replacePlanCoverageBenefits(Set<PlanCoverageBenefit> planCoverageBenefits) {
        this.planCoverageBenefits = planCoverageBenefits;
    }


    public boolean isValidSumAssured(BigDecimal sumAssured) {
        List<BigDecimal> sumAssureds = getAllowedCoverageSumAssuredValues();
        return sumAssureds.contains(sumAssured);
    }

    public List<BigDecimal> getAllowedCoverageSumAssuredValues() {
        List<BigDecimal> allowedValues = Lists.newArrayList();
        if (SumAssuredType.SPECIFIED_VALUES.equals(this.coverageSumAssured.getSumAssuredType())) {
            allowedValues.addAll(this.coverageSumAssured.getSumAssuredValue());
            return allowedValues;
        } else if (SumAssuredType.RANGE.equals(this.coverageSumAssured.getSumAssuredType())) {
            BigDecimal minimumSumAssuredValue = this.coverageSumAssured.getMinSumInsured();
            BigDecimal maximumSumAssuredValue = this.coverageSumAssured.getMaxSumInsured();
            while (minimumSumAssuredValue.compareTo(maximumSumAssuredValue) == -1) {
                allowedValues.add(minimumSumAssuredValue);
                minimumSumAssuredValue = minimumSumAssuredValue.add(new BigDecimal(this.coverageSumAssured.getMultiplesOf()));
            }
            allowedValues.add(this.coverageSumAssured.getMaxSumInsured());
        }
        return allowedValues;
    }


    public Set<Integer> getAllowedCoverageTerm() {
        if (CoverageTermType.SPECIFIED_VALUES.equals(this.coverageTermType)) {
            return this.coverageTerm.getValidTerms();
        } else if (CoverageTermType.POLICY_TERM.equals(this.coverageTermType)) {
            return Sets.newHashSet();
        }
        return this.coverageTerm.getMaturityAges();
    }


    public List<Integer> getAllowedAges() {
        List<Integer> allowedAges = new ArrayList<>();
        int maxAge = this.maxAge;
        int minAge = this.minAge;
        while (minAge <= maxAge) {
            allowedAges.add(minAge);
            minAge = minAge + 1;
        }
        return allowedAges;
    }

    private PlanCoverageBenefit findBenefit(BenefitId benefitId) {
        Optional<PlanCoverageBenefit> planCoverageBenefitOptional = this.planCoverageBenefits.stream().filter(new Predicate<PlanCoverageBenefit>() {
            @Override
            public boolean test(PlanCoverageBenefit planCoverageBenefit) {
                return benefitId.equals(planCoverageBenefit.getBenefitId());
            }
        }).findAny();
        return planCoverageBenefitOptional.isPresent() ? planCoverageBenefitOptional.get() : null;
    }

    public boolean isValidBenefit(BenefitId benefitId){
        PlanCoverageBenefit planCoverageBenefit = findBenefit(benefitId);
        return planCoverageBenefit!=null;
    }

    public boolean isValidBenefitLimit(BenefitId benefitId, BigDecimal sumAssured) {
        PlanCoverageBenefit planCoverageBenefit = findBenefit(benefitId);
        if (planCoverageBenefit == null) {
            return false;
        }
        return planCoverageBenefit.isValidBenefitLimit(sumAssured);
    }
}
