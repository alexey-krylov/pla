package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.CoverageId;
import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public class PlanTest {
    private PlanDetail planDetail;
    private PlanCoverage planCoverage;

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

        PlanCoverage.PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        planCoverage = planCoverageBuilder.withCoverage(new CoverageId("1"))
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

        Plan.PlanBuilder builder = Plan.builder();
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
        Assert.assertEquals(60, premiumTermByAge.getPaymentCutOffAge());
    }

    @Test
    public void should_create_plan_based_on_value() {

        Plan.PlanBuilder builder = Plan.builder();
        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_payment_terms_has_val_gt_than_cut_off_age() {

        Plan.PlanBuilder builder = Plan.builder();
        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 75), 60)
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_when_policy_terms_has_val_gt_than_max_maturity_age() {

        Plan.PlanBuilder builder = Plan.builder();
        builder.withPlanId(new PlanId())
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 65), 60)
                .build();
    }
}
