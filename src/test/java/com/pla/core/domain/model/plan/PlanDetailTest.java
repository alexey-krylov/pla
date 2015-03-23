package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.event.PlanCoverageSumAssuredConfigured;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public class PlanDetailTest {


    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_detail_when_withdrawal_date_less_than_launchDate() {
        PlanDetailBuilder builder = PlanDetail.builder();
        builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(LocalDate.now().plusDays(10))
                .withWithdrawalDate(LocalDate.now().plusDays(3))
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_INSURANCE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER)))
                .withEndorsementTypes(new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.AGENT)))
                .build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_detail_when_group_endorsements_for_individual_insurance() {
        PlanDetailBuilder builder = PlanDetail.builder();
        builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(LocalDate.now().plusDays(10))
                .withWithdrawalDate(LocalDate.now().plusDays(30))
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_INSURANCE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER)))
                .withEndorsementTypes(new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.MEMBER_ADDITION)))
                .build();

    }

    PlanDetail createPlanDetail() {
        PlanDetailBuilder builder = PlanDetail.builder();
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));
        PlanDetail planDetail = builder.withPlanName("Plan 1")
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
        return planDetail;
    }

    @Test
    public void should_create_plan_detail() {
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));

        PlanDetail planDetail = createPlanDetail();
        assertFalse(planDetail.isTaxApplicable());
        assertEquals("Plan 1", planDetail.getPlanName());
        assertEquals("0001900", planDetail.getPlanCode());
        assertEquals(launchDate, planDetail.getLaunchDate());
        assertEquals(withdrawalDate, planDetail.getWithdrawalDate());
        assertEquals(60, planDetail.getMaxEntryAge());
        assertEquals(21, planDetail.getMinEntryAge());
        assertEquals(15, planDetail.getFreeLookPeriod());
        assertEquals(5, planDetail.getSurrenderAfter());
        assertEquals(ClientType.INDIVIDUAL, planDetail.getClientType());
        assertEquals(LineOfBusinessId.INDIVIDUAL_INSURANCE, planDetail.getLineOfBusinessId());
        assertEquals(PlanType.NON_INVESTMENT, planDetail.getPlanType());
        assertEquals(relationshipSet, planDetail.getApplicableRelationships());
        assertEquals(endorsementTypes, planDetail.getEndorsementTypes());
        System.out.println(planDetail);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_add_sum_assured_with_different_coverage() {

        PlanDetail planDetail = createPlanDetail();
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
        planDetail.onPlanCoverageConfigured(new PlanCoverageConfigured(Sets.newHashSet(planCoverage)));
        planDetail.onPlanCoverageSumAssuredConfigured(new PlanCoverageSumAssuredConfigured(new PlanId(), new CoverageId("2"),
                Sets.newHashSet(new BigDecimal(10000),
                        new BigDecimal(50000), new BigDecimal(100000))));
    }
}