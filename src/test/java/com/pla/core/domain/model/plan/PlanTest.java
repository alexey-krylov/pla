package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.BenefitId;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));

        planDetail = builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(launchDate)
                .withWithdrawalDate(withdrawalDate)
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_INSURANCE)
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
                .withDeductibleAsPercentage(new BigDecimal(100))
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
        builder.withMaturityAmounts(Sets.newHashSet(new MaturityAmount(5, new BigDecimal(15))));
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(60, 65), -1);
        Plan plan = builder.build(new PlanId());
        System.out.println(plan);
        Term premiumTermByAge = builder.getPremiumTerm();
        assertEquals(2, premiumTermByAge.getMaturityAges().size());
    }

    @Test
    public void should_create_plan_premium_term_same_as_policy_term() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(60, 65), -1);
        builder.withPremiumTerm(PremiumTermType.REGULAR, null, -1);
        Plan plan = builder.build(new PlanId());
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
        builder.withMaturityAmounts(Sets.newHashSet(new MaturityAmount(5, new BigDecimal(15))));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        Plan plan = builder.build(new PlanId());

    }

    @Test
    public void should_create_plan_based_on_sum_assured_range() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 10000, null, 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withMaturityAmounts(Sets.newHashSet(new MaturityAmount(5, new BigDecimal(15))));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        Plan plan = builder.build(new PlanId());
        SumAssured sumAssured = builder.getSumAssured();
        assertEquals(new BigDecimal(40000000), sumAssured.getMaxSumInsured());
        assertEquals(new BigDecimal(10000000), sumAssured.getMinSumInsured());
        assertEquals(10000, sumAssured.getMultiplesOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_payment_terms_has_val_gt_than_cut_off_age() {

        PlanBuilder builder = Plan.builder();
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 75), 70);
        builder.build(new PlanId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_policy_terms_has_val_gt_than_max_maturity_age() {
        PlanBuilder builder = Plan.builder();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 65), 60);
        builder.build(new PlanId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_based_on_value_when_premium_term_gt_than_policy_term() {
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 75), 70);
        builder.build(new PlanId());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

        Term term = new Term(Sets.newHashSet(10, 15), 15);
        PlanBuilder builder = Plan.builder();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(10, 15), 15);
        builder.withMaturityAmount(5, new BigDecimal(100000));
        Plan plan = builder.build(new PlanId());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();
        PlanBuilder builder = Plan.builder();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40), 45);

        Term term = new Term(Sets.newHashSet(45, 55), 55);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(45, 55), 55);
        Plan plan = builder.build(new PlanId());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

        PlanBuilder builder = Plan.builder();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));

        SumAssured sumAssured = new SumAssured(new BigDecimal(10000000), new BigDecimal(40000000), 10000);
        builder.withSumAssuredForPlanCoverage(coverageId_1, SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 10000, null, 0);
        Plan plan = builder.build(new PlanId());

        PlanCoverage planCoverage = plan.getCoverages().iterator().next();
        assertEquals(sumAssured, planCoverage.getSumAssured());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

        SumAssured sumAssured = new SumAssured(coverageId_1, 12, BigInteger.valueOf(100000));
        PlanBuilder builder = Plan.builder();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withSumAssuredForPlanCoverage(coverageId_1, SumAssuredType.DERIVED, null, new BigDecimal(100000), 0, null, 12);
        Plan plan = builder.build(new PlanId());
        PlanCoverage planCoverage = plan.getCoverages().iterator().next();
        assertEquals(sumAssured, planCoverage.getSumAssured());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.AGE_DEPENDENT, Sets.newHashSet(100, 150, 200), 0);
        builder.build(new PlanId());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(100, 150, 200), 200);
        builder.build(new PlanId());
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
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(100, 150, 200), 0);
        builder.build(new PlanId());
    }


    @Test
    public void should_add_coverage_term_same_as_plan() {
        PlanBuilder builder = Plan.builder();
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withPlanCoverageBenefit(coverageId_1, new BenefitId("Benefit-Coverage-2"),
                CoverageBenefitDefinition.DAY, CoverageBenefitType.AMOUNT,
                new BigDecimal(500), new BigDecimal(5000));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.POLICY_TERM, null, 0);
        Plan plan = builder.build(new PlanId());
        assertEquals(plan.getPolicyTerm().validTerms, Sets.newHashSet(30, 35, 40, 45, 50, 55, 60));
    }


    /**
     * Should not add coverage term of Type Policy if there is no
     * Policy Term configured for the same.
     */
    @Test(expected = IllegalStateException.class)
    public void should_not_add_coverage_term_same_as_plan() {
        PlanBuilder builder = Plan.builder();
        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1));
        builder.withTermForPlanCoverage(coverageId_1, CoverageTermType.POLICY_TERM, null, 0);
        Plan plan = builder.build(new PlanId());
    }

    @Test
    public void should_add_coverage_benefit_when_plan_has_coverage() {

        CoverageId coverageId_1 = new CoverageId("Coverage - 1");
        CoverageId coverageId_2 = new CoverageId("Coverage - 2");

        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage_1 = planCoverageBuilder.withCoverage(coverageId_1)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

        PlanCoverage planCoverage_2 = planCoverageBuilder.withCoverage(coverageId_2)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

        PlanCoverageBenefit planCoverageBenefit_1 = new PlanCoverageBenefit(coverageId_1, new BenefitId("Benefit-Coverage-1"),
                CoverageBenefitDefinition.INCIDENCE, CoverageBenefitType.COVERAGE_LIMIT,
                new BigDecimal(1000), new BigDecimal(100000));

        PlanCoverageBenefit planCoverageBenefit_2 = new PlanCoverageBenefit(coverageId_2, new BenefitId("Benefit-Coverage-2"),
                CoverageBenefitDefinition.DAY, CoverageBenefitType.AMOUNT,
                new BigDecimal(500), new BigDecimal(5000));


        PlanBuilder builder = Plan.builder();
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 10000, null, 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage_1, planCoverage_2));
        builder.withMaturityAmounts(Sets.newHashSet(new MaturityAmount(5, new BigDecimal(15))));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withCoverageBenefits(Sets.newHashSet(planCoverageBenefit_1,
                planCoverageBenefit_2));

        Plan plan = builder.build(new PlanId());

        Iterator<PlanCoverage> planCoverageIter = plan.getCoverages().iterator();

        assertEquals(planCoverageBenefit_2,
                planCoverageIter.next().getPlanCoverageBenefits().toArray()[0]);

        assertEquals(planCoverageBenefit_1,
                planCoverageIter.next().getPlanCoverageBenefits().toArray()[0]);

        assertEquals(CoverageBenefitDefinition.DAY, planCoverageBenefit_2.getDefinedPer());
        assertEquals(CoverageBenefitType.AMOUNT, planCoverageBenefit_2.getCoverageBenefitType());
        assertEquals(new BigDecimal(500), planCoverageBenefit_2.getBenefitLimit());
        assertEquals(new BigDecimal(5000), planCoverageBenefit_2.getMaxLimit());

    }

}


