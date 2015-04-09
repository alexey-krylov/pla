package com.pla.core.domain.model.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.CoverageCover;
import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public class PlanCoverageTest {

    private PlanCoverage planCoverage;
    PlanCoverageBuilder planCoverageBuilder;

    @Before
    public void setUp() {
        CoverageId coverageId = new CoverageId("1");
        planCoverageBuilder = PlanCoverage.builder();
        planCoverage = planCoverageBuilder.withCoverage(coverageId)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withCoverageTerm(CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80)
                .withSumAssuredForPlanCoverage(SumAssuredType.SPECIFIED_VALUES,new BigDecimal(10000),new BigDecimal(100000),1000,Sets.newHashSet(new BigDecimal(100),new BigDecimal(200)),10)
                .withMinAndMaxAge(34, 45)
                .withWaitingPeriod(5)
                .build();
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
                .withDeductible(new BigDecimal(10340))
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
                .withWaitingPeriod(5)
                .build();

        assertEquals(CoverageCover.ACCELERATED, planCoverage.getCoverageCover());
        assertEquals(Boolean.FALSE, planCoverage.getTaxApplicable());
        assertEquals(planCoverage.getCoverageType(), CoverageType.BASE);
        assertEquals(45, planCoverage.getMaxAge());
        assertEquals(21, planCoverage.getMinAge());
        assertEquals(CoverageCover.ACCELERATED, planCoverage.getCoverageCover());
        assertEquals(5, planCoverage.getWaitingPeriod());
    }

    @Test
    public void givenPlanCoverage_whenSumAssuredTypeIsSpecifiedValues_thenItShouldReturnTheListOfSumAssuredValues(){
        List<BigDecimal> expectedSumAssuredValues = Lists.newArrayList(new BigDecimal(100), new BigDecimal(200));
        List<BigDecimal>  allowedCoverageSumAssuredValues = planCoverage.getAllowedCoverageSumAssuredValues();
        assertThat(expectedSumAssuredValues, is(allowedCoverageSumAssuredValues));
    }

    @Test
    public void givenPlanCoverage_whenSumAssuredTypeIsRange_thenItShouldTheSumAssuredValuesWithTheMultipleOfMinAndMaxAssuredValue(){
        planCoverage = planCoverageBuilder.withSumAssuredForPlanCoverage(SumAssuredType.RANGE,new BigDecimal(10000),new BigDecimal(40000),10000,Sets.newHashSet(new BigDecimal(100),new BigDecimal(200)),10).build();
        List<BigDecimal> expectedSumAssuredValues = Lists.newArrayList(new BigDecimal(10000), new BigDecimal(20000),new BigDecimal(30000), new BigDecimal(40000));
        List<BigDecimal> allowedCoverageSumAssuredValues = planCoverage.getAllowedCoverageSumAssuredValues();
        assertThat(expectedSumAssuredValues, is(allowedCoverageSumAssuredValues));
    }

    @Test
    public void givenPlanCoverage_whenCoverageTermTypeIsSpecifiedValue_thenItShouldReturnSetOfValidTerm(){
        Set<Integer> expectedValidTerm  = Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);
        Set<Integer> allowedCoverageTerm  = planCoverage.getAllowedCoverageTerm();
        assertThat(expectedValidTerm,is(allowedCoverageTerm));
    }

    @Test
    public void givenPlanCoverage_whenCoverageTermTypeIsPolicyTermAndSpecifiedValues_thenItShouldReturnSetOfMaturityAges(){
        planCoverage  = planCoverageBuilder.withCoverageTerm(CoverageTermType.AGE_DEPENDENT, Sets.newHashSet(30, 35, 40, 45), 80).build();
        Set<Integer> expectedMaturityAged  = Sets.newHashSet(30, 35, 40, 45);
        Set<Integer> allowedMaturityAges  = planCoverage.getAllowedCoverageTerm();
        assertThat(expectedMaturityAged,is(allowedMaturityAges));
    }

    @Test
    public void givenPlanCoverageWithMinAndMaxAge_thenItShouldReturnTheAllowedAges(){
        List<Integer> expectedAllowedAges = Lists.newArrayList(34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45);
        List<Integer> allowedAges  =  planCoverage.getAllowedAges();
        assertThat(expectedAllowedAges,is(allowedAges));
    }

}
