package com.pla.core.domain.model.plan;

import com.pla.core.domain.model.CoverageId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@EqualsAndHashCode(of = {"coverage"})
@ToString
@Getter(AccessLevel.PACKAGE)
public class PlanCoverage {


    private CoverageId coverageId;
    private CoverageType coverageType;
    private CoverageCover coverageCover;
    private BigDecimal deductibleAmount;
    private BigDecimal deductiblePercentage;
    private int waitingPeriod;
    private int minAge;
    private int maxAge;
    private Boolean taxApplicable;

    public PlanCoverage(PlanCoverageBuilder builder) {
        checkArgument(builder.coverageId != null, "Coverage is mandatory.");
        this.coverageId = builder.coverageId;

        checkArgument(builder.coverageCover != null, " Coverage Cover has to be one of %s", CoverageCover.values());
        this.coverageCover = builder.coverageCover;

        checkArgument(builder.coverageType != null, " Coverage Type is mandatory. It can be one of %s", CoverageType.values());
        this.coverageType = builder.coverageType;

        if (builder.deductibleAmount != null) {
            checkArgument(BigDecimal.ZERO.compareTo(builder.deductibleAmount) == -1, "Deductible Amount has to be greater than 0");
            this.deductibleAmount = builder.deductibleAmount;
        }

        if (builder.deductiblePercentage != null) {
            checkArgument(BigDecimal.ZERO.compareTo(builder.deductiblePercentage) == -1, "Deductible Percentage has to be greater than 0");
            this.deductiblePercentage = builder.deductiblePercentage;
        }

        if (this.deductiblePercentage != null && this.deductibleAmount != null)
            throw new IllegalArgumentException("Cannot create Plan Coverage with Deductible Percentage and Amount.");

        this.waitingPeriod = builder.waitingPeriod;
        checkArgument(builder.minAge > 0, "Min Age for this coverage is missing.");
        this.minAge = builder.minAge;

        checkArgument(builder.maxAge > 0, "Max Entry Age for this coverage is missing.");
        this.maxAge = builder.maxAge;

        checkArgument(builder.taxApplicable != null, "Tax Applicable is mandatory.");
        this.taxApplicable = builder.taxApplicable;
    }

    public static PlanCoverageBuilder builder() {
        return new PlanCoverageBuilder();
    }


    public static class PlanCoverageBuilder {

        private CoverageId coverageId;
        private CoverageType coverageType;
        private CoverageCover coverageCover;
        private BigDecimal deductibleAmount;
        private BigDecimal deductiblePercentage;
        private int waitingPeriod;
        private int minAge;
        private int maxAge;
        private Boolean taxApplicable;

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

        public PlanCoverageBuilder withDeductibleAmount(BigDecimal deductibleAmount) {
            this.deductibleAmount = deductibleAmount;
            return this;
        }

        public PlanCoverageBuilder withDeductibleAsPercentage(BigDecimal deductiblePercentage) {
            this.deductiblePercentage = deductiblePercentage;
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
            checkArgument(minAge < maxAge);
            this.minAge = minAge;
            this.maxAge = maxAge;
            return this;
        }

        public PlanCoverage build() {
            return new PlanCoverage(this);
        }

    }
}
