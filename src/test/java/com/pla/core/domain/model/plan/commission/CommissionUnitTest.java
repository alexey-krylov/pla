package com.pla.core.domain.model.plan.commission;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.repository.PlanRepository;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by User on 4/7/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommissionUnitTest {
    @Mock
    Plan plan;

    @Mock
    PlanRepository planRepository;

    @Test
    public void givenCommissionTermsWithOverlappingYearShouldReturnFalse(){
        Commission commission =  new Commission();
        Set<CommissionTerm> commissionTermSet = Sets.newHashSet();
        commissionTermSet.add(new CommissionTerm(new Integer(5), new Integer(10), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(7), new Integer(11), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(3), new Integer(12), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(3), new Integer(12), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(12), new Integer(15), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(17), new Integer(20), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(1), new Integer(21), new BigDecimal(6.00), CommissionTermType.RANGE));
        assertFalse(commission.validateOverLappingYears(commissionTermSet));
    }

    @Test
    public void givenCommissionTermsWithoutOverlappingYearShouldReturnTrue(){
        Commission commission =  new Commission();
        Set<CommissionTerm> commissionTermSet = Sets.newHashSet();
        commissionTermSet.add(new CommissionTerm(new Integer(34), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(46), new Integer(54), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(55), new Integer(89), new BigDecimal(6.00), CommissionTermType.RANGE));
        assertTrue(commission.validateOverLappingYears(commissionTermSet));
    }
    @Test
    public void givenCommissionTermsWithoutOverlappingYearOfDifferentRangeShouldReturnTrue(){
        Commission commission =  new Commission();
        Set<CommissionTerm> commissionTermSet = Sets.newHashSet();
        commissionTermSet.add(new CommissionTerm(new Integer(34), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(78), new Integer(100), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTermSet.add(new CommissionTerm(new Integer(123), new Integer(189), new BigDecimal(6.00), CommissionTermType.RANGE));
        assertTrue(commission.validateOverLappingYears(commissionTermSet));
    }

    @Test
    public void givenCommissionItShouldAddCommissionTerms(){
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        Set<Integer> policyTerms = Sets.newHashSet();
        policyTerms.add(new Integer(34));
        policyTerms.add(new Integer(45));
        policyTerms.add(new Integer(89));
        when(plan.getAllowedPolicyTerm()).thenReturn(policyTerms);
        commissionTerms.add(new CommissionTerm(new Integer(34), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(46), new Integer(54), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(55), new Integer(89), new BigDecimal(6.00), CommissionTermType.RANGE));
        assertEquals(3, commission.addCommissionTerm(commissionTerms, plan).getCommissionTerms().size());

    }

    @Test
    public void givenCommissionItShouldUpdateCommissionTerms() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(46), new Integer(54), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(55), new Integer(89), new BigDecimal(6.00), CommissionTermType.RANGE));
        Set<Integer> policyTerms = Sets.newHashSet();
        policyTerms.add(new Integer(10));
        policyTerms.add(new Integer(45));
        policyTerms.add(new Integer(54));
        policyTerms.add(new Integer(100));
        when(plan.getAllowedPolicyTerm()).thenReturn(policyTerms);
        commission.addCommissionTerm(commissionTerms, plan).getCommissionTerms();
        Set<CommissionTerm> updatedCommissionTerms = Sets.newHashSet();
        updatedCommissionTerms.add(new CommissionTerm(new Integer(34), new Integer(38), new BigDecimal(10.00), CommissionTermType.RANGE));
        updatedCommissionTerms.add(new CommissionTerm(new Integer(46), new Integer(48), new BigDecimal(15.00), CommissionTermType.RANGE));
        updatedCommissionTerms.add(new CommissionTerm(new Integer(55), new Integer(99), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertThat(commission.updateWithCommissionTerms(updatedCommissionTerms, plan).getCommissionTerms(), CoreMatchers.hasItems(new CommissionTerm(new Integer(34), new Integer(38), new BigDecimal(10.00), CommissionTermType.RANGE), new CommissionTerm(new Integer(46), new Integer(48), new BigDecimal(15.00),
                CommissionTermType.RANGE), new CommissionTerm(new Integer(55), new Integer(99), new BigDecimal(6.00), CommissionTermType.RANGE)));

    }

    @Test
    public void givenCommissionTermsWithEndYearGreaterThanMaximumMaturityAgeItShouldReturnFalse() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(46), new Integer(56), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(34), new Integer(67), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertFalse(commission.isLesserThanMaxPlanPolicyTerm(commissionTerms, 55));

    }

    @Test
    public void givenCommissionTermsWithEndYearLesserThanMaximumMaturityAgeItShouldReturnTrue() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(46), new Integer(54), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(34), new Integer(50), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertTrue(commission.isLesserThanMaxPlanPolicyTerm(commissionTerms, 55));

    }

    @Test
    public void givenOneCommissionTermWithEndYearGreaterThanMaximumMaturityAgeItShouldReturnFalse() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(58), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertFalse(commission.isLesserThanMaxPlanPolicyTerm(commissionTerms, 55));

    }

    @Test
    public void givenOneCommissionTermWithEndYearLesserThanMaximumMaturityAgeItShouldReturnTrue() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertTrue(commission.isLesserThanMaxPlanPolicyTerm(commissionTerms, 55));

    }

    @Test
    public void givenCommissionTermsWithStartYearLesserThanMinimumMaturityAgeItShouldReturnFalse() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(46), new Integer(56), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(34), new Integer(67), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertFalse(commission.isGreaterThanMinPlanPolicyTerm(commissionTerms, 15));

    }

    @Test
    public void givenOneCommissionTermWithStartYearLesserThanMinimumMaturityAgeItShouldReturnFalse() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertFalse(commission.isGreaterThanMinPlanPolicyTerm(commissionTerms, 15));

    }

    @Test
    public void givenCommissionTermsWithStartYearGreaterThanMinimumMaturityAgeItShouldReturnTrue() {
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(12), new Integer(45), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(56), new Integer(78), new BigDecimal(6.00), CommissionTermType.RANGE));
        commissionTerms.add(new CommissionTerm(new Integer(2), new Integer(4), new BigDecimal(6.00), CommissionTermType.RANGE));

        assertTrue(commission.isGreaterThanMinPlanPolicyTerm(commissionTerms, 1));

    }


}
