package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.BenefitId;
import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
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
        PlanDetail.PlanDetailBuilder builder = PlanDetail.builder();
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
        Plan plan = builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnAge(60)
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnAge(60)
                .build();

        System.out.println(plan);
        PremiumPayment premiumTermByAge = plan.getPlanPayment().getPremiumPayment();
        assertEquals(60, premiumTermByAge.getPaymentCutOffAge());
    }

    @Test
    public void should_create_plan_based_on_value() {

        PlanBuilder builder = Plan.builder();
        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();

    }

    @Test
    public void should_create_plan_based_on_sum_assured_range() {

        PlanBuilder builder = Plan.builder();
        Plan plan = builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredByRange(new BigDecimal(10000000), new BigDecimal(40000000), 1000)
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();

        SumAssuredByRange sumAssured = ((SumAssuredByRange) plan.getSumAssured());
        System.out.println(sumAssured);
        assertEquals(new BigDecimal(40000000), sumAssured.getMaxSumInsured());
        assertEquals(new BigDecimal(10000000), sumAssured.getMinSumInsured());
        assertEquals(1000, sumAssured.getMultiplesOf());

    }

    @Test(expected = IllegalStateException.class)
    public void should_not_create_plan_when_payment_terms_has_val_gt_than_cut_off_age() {

        PlanBuilder builder = Plan.builder();
        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 75))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 75)
                .build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_policy_terms_has_val_gt_than_max_maturity_age() {

        PlanBuilder builder = Plan.builder();
        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 65), 60)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void should_not_create_plan_based_on_value_when_premium_term_gt_than_policy_term() {

        PlanBuilder builder = Plan.builder();
        Plan plan = builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 75))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();


    }


    @Test
    public void should_add_coverage_benefit_when_plan_has_coverage() {
        PlanBuilder builder = Plan.builder();

        CoverageId coverageId_1 = new CoverageId("1111");
        CoverageId coverageId_2 = new CoverageId("2222");

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

        PlanCoverageBenefit planCoverageBenefit_1 = new PlanCoverageBenefit(coverageId_1, new BenefitId("111"),
                CoverageBenefitDefinition.INCIDENCE, CoverageBenefitType.COVERAGE_LIMIT,
                new BigDecimal(1000), new BigDecimal(100000));

        PlanCoverageBenefit planCoverageBenefit_2 = new PlanCoverageBenefit(coverageId_2, new BenefitId("222"),
                CoverageBenefitDefinition.DAY, CoverageBenefitType.AMOUNT,
                new BigDecimal(500), new BigDecimal(5000));
        System.out.println(planCoverageBenefit_1);

        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredByRange(new BigDecimal(10000000), new BigDecimal(40000000), 1000)
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60))
                .withCoverages(Sets.newHashSet(planCoverage_1, planCoverage_2))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .withCoverageBenefit(coverageId_1, planCoverageBenefit_1)
                .withCoverageBenefit(coverageId_2, planCoverageBenefit_2)
                .build();

        assertEquals(planCoverageBenefit_1,
                planCoverage_1.getPlanCoverageBenefits().toArray()[0]);

        assertEquals(planCoverageBenefit_2,
                planCoverage_2.getPlanCoverageBenefits().toArray()[0]);

        assertEquals(CoverageBenefitDefinition.DAY, planCoverageBenefit_2.getDefinedPer());
        assertEquals(CoverageBenefitType.AMOUNT, planCoverageBenefit_2.getCoverageBenefitType());
        assertEquals(new BigDecimal(500), planCoverageBenefit_2.getBenefitLimit());
        assertEquals(new BigDecimal(5000), planCoverageBenefit_2.getMaxLimit());

    }

}


