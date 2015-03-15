package com.pla.core.domain.model.plan;

import com.pla.core.domain.model.CoverageId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@EqualsAndHashCode(of = {"coverageId"})
@ToString(exclude = {"plan"})
@Getter(AccessLevel.PACKAGE)
@Entity
public class PlanCoverage {

    @Id()
    private CoverageId coverageId;

    @Column(nullable = false)
    private CoverageType coverageType;
    @Column(nullable = false)
    private CoverageCover coverageCover;
    private BigDecimal deductibleAmount;
    private BigDecimal deductiblePercentage;
    private int waitingPeriod;
    private int minAge;
    private int maxAge;
    @Column(nullable = false)
    private Boolean taxApplicable;
    /**
     * Holds the Benefits that are applicable for Plan.
     */
    @ElementCollection
    @CollectionTable(name = "plan_coverage_benefit", joinColumns = @JoinColumn(name = "plan_coverage_id"))
    private Set<PlanCoverageBenefit> planCoverageBenefits = new HashSet<PlanCoverageBenefit>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    protected PlanCoverage() {
    }

    /**
     * This constructor is added for Hibernate OneToMany.
     * This is only used internally while creating the Plan.
     *
     * This is not for public usage.
     *
     * @param plan
     * @param planCoverage
     */
    PlanCoverage(Plan plan, PlanCoverage planCoverage) {
        this.plan = plan;
        this.coverageId = planCoverage.coverageId;
        this.coverageCover = planCoverage.coverageCover;
        this.coverageType = planCoverage.coverageType;
        this.deductibleAmount = planCoverage.deductibleAmount;
        this.deductiblePercentage = planCoverage.deductiblePercentage;
        this.waitingPeriod = planCoverage.waitingPeriod;
        this.minAge = planCoverage.minAge;
        this.maxAge = planCoverage.maxAge;
        this.taxApplicable = planCoverage.taxApplicable;
        planCoverageBenefits = planCoverage.planCoverageBenefits;
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
        planCoverageBenefits = new HashSet<PlanCoverageBenefit>();
    }

    public static PlanCoverageBuilder builder() {
        return new PlanCoverageBuilder();
    }

    public void addCoverageBenefit(CoverageId coverageId, PlanCoverageBenefit planCoverageBenefit) {
        checkArgument(coverageId.equals(this.coverageId));
        planCoverageBenefits.add(planCoverageBenefit);
    }

}
