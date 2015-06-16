package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.EndorsementType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.joda.time.DateTime;
import org.junit.Test;

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


    DateTime launchDate = DateTime.now().plusDays(10);
    DateTime withdrawalDate = DateTime.now().plusDays(30);

    @Test(expected = IllegalArgumentException.class)
    public void should_not_create_plan_detail_when_withdrawal_date_less_than_launchDate() {
        PlanDetailBuilder builder = PlanDetail.builder();
        builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(DateTime.now().plusDays(10))
                .withWithdrawalDate(DateTime.now().plusDays(3))
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withLineOfBusinessId(LineOfBusinessEnum.INDIVIDUAL_LIFE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER)))
                .withEndorsementTypes(Sets.newHashSet(new EndorsementType("Change Insured Name")))
                .build();

    }

    PlanDetail createPlanDetail() {
        PlanDetailBuilder builder = PlanDetail.builder();
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        PlanDetail planDetail = builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(launchDate)
                .withWithdrawalDate(withdrawalDate)
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessEnum.INDIVIDUAL_LIFE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(relationshipSet)
                .withTaxApplicable(false)
                .withEndorsementTypes(Sets.newHashSet(new EndorsementType("Change Insured Name")))
                .build();
        return planDetail;
    }

    @Test
    public void should_create_plan_detail() {
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
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
        assertEquals(LineOfBusinessEnum.INDIVIDUAL_LIFE, planDetail.getLineOfBusinessId());
        assertEquals(PlanType.NON_INVESTMENT, planDetail.getPlanType());
        assertEquals(relationshipSet, planDetail.getApplicableRelationships());
    }


}
