package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public class PlanTest {
    private PlanDetail planDetail;
    private PlanCoverage planCoverage;
    private CoverageId coverageId = new CoverageId("1");

    @Before
    public void setUp() {
        PlanDetailBuilder builder = PlanDetail.builder();
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.IND_CHANGE_ADDRESS, EndorsementType.IND_CHANGE_NAME));

        planDetail = builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(launchDate)
                .withWithdrawalDate(withdrawalDate)
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_LIFE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(relationshipSet)
                .withEndorsementTypes(endorsementTypes)
                .withTaxApplicable(false)
                .build();

        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        planCoverage = planCoverageBuilder.withCoverage(coverageId)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .build();

    }

    @Test
    public void should_create_plan_based_on_age() {

        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.SPECIFIED_VALUES, null, new BigDecimal(50000000), 0,
                Sets.newTreeSet(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000))), 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_AGES, Sets.newHashSet(45, 55), -1);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(60, 65), -1,null);
        Plan plan = builder.build();
        Term premiumTermByAge = builder.getPremiumTerm();
        assertEquals(2, premiumTermByAge.getMaturityAges().size());
    }

    @Test
    public void should_create_plan_premium_term_same_as_policy_term() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(60, 65), -1,null);
        builder.withPremiumTerm(PremiumTermType.REGULAR, null, -1);
        Plan plan = builder.build();
        Term premiumTerm = builder.getPremiumTerm();
        Term policyTerm = builder.getPolicyTerm();
        assertEquals(premiumTerm, policyTerm);
    }

    @Test
    public void should_create_plan_based_on_value() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.SPECIFIED_VALUES, null, new BigDecimal(50000000), 0,
                Sets.newTreeSet(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000))), 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60,null);
        Plan plan = builder.build();

    }

    @Test
    public void should_create_plan_based_on_sum_assured_range() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 10000, null, 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60,null);
        Plan plan = builder.build();
        SumAssured sumAssured = builder.getSumAssured();
        assertEquals(new BigDecimal(40000000), sumAssured.getMaxSumInsured());
        assertEquals(new BigDecimal(10000000), sumAssured.getMinSumInsured());
        assertEquals(10000, sumAssured.getMultiplesOf());
    }

    /*
    Bug ID: 0010778
    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_payment_terms_has_val_gt_than_cut_off_age() {

        PlanBuilder builder = Plan.builder();
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 75), 70);
        builder.build();

    }*/


    /*
    Bug ID: 0010797
    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_policy_terms_has_val_gt_than_max_maturity_age() {
        PlanBuilder builder = Plan.builder();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 65), 60);
        builder.build();

    }*/

    @Test(expected = IllegalStateException.class)
    public void should_not_create_plan_based_on_value_when_premium_term_gt_than_policy_term() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60,null);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 75), 70);
        builder.build();
    }


    @Test
    public void should_add_coverage_term_to_plan() {
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .withCoverageTerm(CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(10, 15), 15)
                .build();

        Term term = new Term(Sets.newHashSet(10, 15), 15);
        PlanBuilder builder = Plan.builder();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        Plan plan = builder.build();
        PlanCoverage planCoverage = builder.getCoverages().iterator().next();
        assertEquals(term, planCoverage.getCoverageTerm());
    }


    @Test(expected = IllegalStateException.class)
    public void should_not_add_coverage_term_gt_than_policy_term() {
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .withCoverageTerm(CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(45, 55, 60), 60)
                .build();
        PlanBuilder builder = Plan.builder();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40), 45,null);

        Term term = new Term(Sets.newHashSet(45, 55), 55);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        Plan plan = builder.build();
        assertEquals(term, builder.getCoverages().iterator().next().getCoverageTerm());
    }


    @Test
    public void should_add_coverage_sum_assured_to_plan() {
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .withSumAssuredForPlanCoverage(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 10000,
                        null, 0)
                .build();

        PlanBuilder builder = Plan.builder();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));

        SumAssured sumAssured = new SumAssured(new BigDecimal(10000000), new BigDecimal(40000000), 10000);
        Plan plan = builder.build();

        PlanCoverage planCoverage = plan.getCoverages().iterator().next();
        assertEquals(sumAssured, planCoverage.getCoverageSumAssured());
    }


    @Test
    public void should_add_coverage_derived_sum_assured_to_plan() {
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .withSumAssuredForPlanCoverage(SumAssuredType.DERIVED, null, new BigDecimal(100000), 0, null, 12)
                .build();

        SumAssured sumAssured = new SumAssured(coverageId_1, 12, BigInteger.valueOf(100000));
        PlanBuilder builder = Plan.builder();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        Plan plan = builder.build();
        PlanCoverage planCoverage = plan.getCoverages().iterator().next();
        assertEquals(sumAssured, planCoverage.getCoverageSumAssured());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_add_derived_sum_assured_to_plan() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanSumAssured(SumAssuredType.DERIVED, null, null, 0, null, 0);
    }

    @Test
    public void should_add_plan_coverage_term_to_plan() {
        PlanBuilder builder = Plan.builder();
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60,null);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.build();
    }


    @Test(expected = IllegalStateException.class)
    public void should_not_add_plan_coverage_term_to_plan() {
        PlanBuilder builder = Plan.builder();
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .withCoverageTerm(CoverageTermType.SPECIFIED_VALUES,
                        Sets.newHashSet(80), 80)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60,null);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.build();
    }


    @Test(expected = IllegalArgumentException.class)
    public void should_not_add_plan_coverage_term_without_max_maturity_age_to_plan() {
        PlanBuilder builder = Plan.builder();
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withCoverageTerm(CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(30, 35, 40), 0)
                .withWaitingPeriod(5)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60,null);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.build();
    }


}


