package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.CoverageCover;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.identifier.CoverageId;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 14/03/2015
 */
class PlanCoverageBuilder {

    CoverageId coverageId;
    CoverageType coverageType;
    CoverageCover coverageCover;
    BigDecimal deductibleAmount;
    BigDecimal deductiblePercentage;
    int waitingPeriod;
    int minAge;
    int maxAge;
    Boolean taxApplicable;

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
