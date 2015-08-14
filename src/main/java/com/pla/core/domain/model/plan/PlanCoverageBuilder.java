package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 14/03/2015
 */
public class PlanCoverageBuilder {

    Set<MaturityAmount> maturityAmounts = new HashSet<>();
    CoverageId coverageId;
    String coverageCode;
    String coverageName;
    CoverageType coverageType;
    CoverageCover coverageCover;
    BigDecimal deductibleAmount;
    int waitingPeriod;
    int minAge;
    int maxAge;
    Boolean taxApplicable;
    String deductibleType;
    SumAssured sumAssured;
    Term coverageTerm;
    CoverageTermType coverageTermType;
    List<MaturityAmount> maturityAmount;
    Set<PlanCoverageBenefit> planCoverageBenefits = new HashSet<PlanCoverageBenefit>();

    public PlanCoverageBuilder withCoverage(CoverageId coverageId) {
        this.coverageId = coverageId;
        return this;
    }

    public PlanCoverageBuilder withCoverageType(CoverageType coverageType) {
        this.coverageType = coverageType;
        return this;
    }

    public PlanCoverageBuilder withCoverageCover(CoverageCover coverageCover) {
        this.coverageCover = coverageCover;
        return this;
    }

    public PlanCoverageBuilder withCoverageCode(String coverageCode) {
        this.coverageCode = coverageCode;
        return this;
    }

    public PlanCoverageBuilder withCoverageName(String coverageName) {
        this.coverageName = coverageName;
        return this;
    }

    public PlanCoverageBuilder withDeductible(BigDecimal deductibleAmount) {
        this.deductibleAmount = deductibleAmount;
        return this;
    }

    public PlanCoverageBuilder withWaitingPeriod(int waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
        return this;
    }

    public PlanCoverageBuilder withTaxApplicable(boolean taxApplicable) {
        this.taxApplicable = taxApplicable;
        return this;
    }

    public PlanCoverageBuilder withMinAndMaxAge(int minAge, int maxAge) {
        checkArgument(minAge <= maxAge,"Minimum Age should be less than Maximum Age");
        this.minAge = minAge;
        this.maxAge = maxAge;
        return this;
    }

    public PlanCoverageBuilder withDeductibleType(String deductibleType) {
        this.deductibleType = deductibleType;
        return this;
    }

    /**
     * @param coverageTermType
     * @param validTerms
     * @param maxMaturityAge
     * @return
     * @deprecated Please use #withCoverageTerm(CoverageTermType,Term)
     */
    @Deprecated
    PlanCoverageBuilder withCoverageTerm(CoverageTermType coverageTermType, Set<Integer> validTerms, int maxMaturityAge) {
        switch (coverageTermType) {
            case SPECIFIED_VALUES:
                checkArgument(maxMaturityAge > 0);
                checkArgument(validTerms != null);
                this.coverageTerm = new Term(validTerms, maxMaturityAge);
                break;
            case AGE_DEPENDENT:
                checkArgument(validTerms != null);
                this.coverageTerm = new Term(validTerms);
                break;
            case POLICY_TERM:
                break;
        }
        this.coverageTermType = coverageTermType;
        return this;
    }

    //TODO Refactor this to send the Term instead of validTerms and maxMaturityAge
    public PlanCoverageBuilder withCoverageTerm(CoverageTermType coverageTermType, Term coverageTerm) {
        switch (coverageTermType) {
            case SPECIFIED_VALUES:
                checkArgument(coverageTerm != null);
                this.coverageTerm = new Term(coverageTerm);
                break;
            case AGE_DEPENDENT:
                checkArgument(coverageTerm != null);
                this.coverageTerm = new Term(coverageTerm);
                break;
            case POLICY_TERM:
                break;
        }
        this.coverageTermType = coverageTermType;
        return this;
    }

    public PlanCoverageBuilder withMaturityAmount(int maturityYear, BigDecimal guaranteedSurvivalBenefitAmount) {
        this.maturityAmounts.add(new MaturityAmount(maturityYear, guaranteedSurvivalBenefitAmount));
        return this;
    }


    public PlanCoverageBuilder withSumAssuredForPlanCoverage(SumAssuredType sumAssuredType,
                                                             BigDecimal minSumAssuredAmount,
                                                             BigDecimal maxSumAssuredAmount,
                                                             int multiplesOf,
                                                             Set<BigDecimal> assuredValues,
                                                             int percentage) {
        switch (sumAssuredType) {
            case SPECIFIED_VALUES:
                Preconditions.checkArgument(UtilValidator.isNotEmpty(assuredValues));
                this.sumAssured = new SumAssured(new TreeSet<>(assuredValues));
                break;
            case DERIVED:
                Preconditions.checkArgument(percentage > 0);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1);
                Preconditions.checkArgument(coverageId != null);
                this.sumAssured = new SumAssured(coverageId, percentage, BigInteger.valueOf(maxSumAssuredAmount.longValue()));
                break;
            case RANGE:
                this.sumAssured = new SumAssured(minSumAssuredAmount, maxSumAssuredAmount, multiplesOf);
                break;
        }
        return this;
    }

    public PlanCoverageBuilder withMaturityAmount(List<MaturityAmount> maturityAmount) {
        this.maturityAmount = maturityAmount;
        return this;
    }

    //TODO Change benefitId String to BenefitId type
    public PlanCoverageBuilder withBenefitLimit(
            CoverageId coverageId, String coverageName, String benefitName,
            String benefitId, CoverageBenefitDefinition definedPer,
                                                CoverageBenefitType coverageBenefitType,
                                                BigDecimal benefitLimit,
                                                BigDecimal maxLimit) {
        this.planCoverageBenefits.add(new PlanCoverageBenefit(coverageId, coverageName, benefitName, benefitId, definedPer, coverageBenefitType,
                benefitLimit, maxLimit));
        return this;
    }


    public PlanCoverage build() {
        return new PlanCoverage(this);
    }

}
