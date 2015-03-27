package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.CoverageCover;
import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.MaturityAmount;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@EqualsAndHashCode(of = {"coverageId"})
@ToString()
@Getter(AccessLevel.PACKAGE)
public class PlanCoverage {

    private CoverageId coverageId;
    private CoverageCover coverageCover;
    private CoverageType coverageType;
    private String deductibleType;
    private BigDecimal deductibleAmount;
    private int waitingPeriod;
    private int minAge;
    private int maxAge;
    private Boolean taxApplicable;
    private SumAssured sumAssured;
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
        this.sumAssured = builder.sumAssured;
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
}
