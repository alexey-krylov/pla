package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.CoverageId;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public class PlanCoverageTest {


    @Before
    public void setUp() {
    }

    @Test
    public void should_create_coverage_with_out_deductible() {
        PlanCoverageBuilder builder = PlanCoverage.builder();
        PlanCoverage planCoverage = builder.withCoverage(new CoverageId("1"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .build();

        assertEquals(45, planCoverage.getMaxAge());
        assertEquals(21, planCoverage.getMinAge());
    }

    @Test
    public void should_create_coverage_with_all_parameters() {
        PlanCoverageBuilder builder = PlanCoverage.builder();
        PlanCoverage planCoverage = builder.withCoverage(new CoverageId("1"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAmount(new BigDecimal(10340))
                .withWaitingPeriod(5)
                .build();

        assertEquals(CoverageCover.ACCELERATED, planCoverage.getCoverageCover());
        assertEquals(Boolean.FALSE, planCoverage.getTaxApplicable());
        assertEquals(planCoverage.getCoverageType(), CoverageType.BASE);
        assertEquals(45, planCoverage.getMaxAge());
        assertEquals(21, planCoverage.getMinAge());
        assertEquals(CoverageCover.ACCELERATED, planCoverage.getCoverageCover());
        assertEquals(new BigDecimal(10340), planCoverage.getDeductibleAmount());
        assertEquals(5, planCoverage.getWaitingPeriod());
    }

    @Test
    public void should_create_coverage_with_deductible_percentage() {
        PlanCoverageBuilder builder = PlanCoverage.builder();
        PlanCoverage planCoverage = builder.withCoverage(new CoverageId("1"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

        assertEquals(CoverageCover.ACCELERATED, planCoverage.getCoverageCover());
        assertEquals(Boolean.FALSE, planCoverage.getTaxApplicable());
        assertEquals(planCoverage.getCoverageType(), CoverageType.BASE);
        assertEquals(45, planCoverage.getMaxAge());
        assertEquals(21, planCoverage.getMinAge());
        assertEquals(CoverageCover.ACCELERATED, planCoverage.getCoverageCover());
        assertEquals(new BigDecimal(100), planCoverage.getDeductiblePercentage());
        assertEquals(5, planCoverage.getWaitingPeriod());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_coverage_with_both_deductible_percentage_and_amount() {
        PlanCoverageBuilder builder = PlanCoverage.builder();
        PlanCoverage planCoverage = builder.withCoverage(new CoverageId("1"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withDeductibleAmount(new BigDecimal(1800))
                .withWaitingPeriod(5)
                .build();
    }


    @Test(expected = IllegalArgumentException.class)
    public void cannot_add_benefits_belonging_to_different_coverages_to_same_plan_coverage() {
        PlanCoverageBuilder builder = PlanCoverage.builder();
        PlanCoverage planCoverage = builder.withCoverage(new CoverageId("1"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withDeductibleAmount(new BigDecimal(1800))
                .withWaitingPeriod(5)
                .build();
    }
}
