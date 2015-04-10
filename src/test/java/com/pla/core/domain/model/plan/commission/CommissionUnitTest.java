package com.pla.core.domain.model.plan.commission;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by User on 4/7/2015.
 */
public class CommissionUnitTest {

    @Test
    public void givenCommissionTermsWithOverlappingYearShouldReturnFalse(){
        Commission commission =  new Commission();
        Set<CommissionTerm> commissionTermSet = Sets.newHashSet();
        commissionTermSet.add(new CommissionTerm(new Integer(5),new Integer(10),new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(7),new Integer(11),new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(3),new Integer(12), new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(3),new Integer(12), new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(12),new Integer(15), new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(17),new Integer(20), new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(1),new Integer(21), new BigDecimal(6.00)));
        assertFalse(commission.validateOverLappingYears(commissionTermSet));
    }

    @Test
    public void givenCommissionTermsWithoutOverlappingYearShouldReturnTrue(){
        Commission commission =  new Commission();
        Set<CommissionTerm> commissionTermSet = Sets.newHashSet();
        commissionTermSet.add(new CommissionTerm(new Integer(34),new Integer(45),new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(46),new Integer(54),new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(55),new Integer(89), new BigDecimal(6.00)));
        assertTrue(commission.validateOverLappingYears(commissionTermSet));
    }
    @Test
    public void givenCommissionTermsWithoutOverlappingYearOfDifferentRangeShouldReturnTrue(){
        Commission commission =  new Commission();
        Set<CommissionTerm> commissionTermSet = Sets.newHashSet();
        commissionTermSet.add(new CommissionTerm(new Integer(34),new Integer(45),new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(78),new Integer(100),new BigDecimal(6.00)));
        commissionTermSet.add(new CommissionTerm(new Integer(123),new Integer(189), new BigDecimal(6.00)));
        assertTrue(commission.validateOverLappingYears(commissionTermSet));
    }

    @Test
    public void givenCommissionItShouldAddCommissionTerms(){
        Commission commission = new Commission();
        Set<CommissionTerm> commissionTerms = Sets.newHashSet();
        commissionTerms.add(new CommissionTerm(new Integer(34),new Integer(45),new BigDecimal(6.00)));
        commissionTerms.add(new CommissionTerm(new Integer(46),new Integer(54),new BigDecimal(6.00)));
        commissionTerms.add(new CommissionTerm(new Integer(55),new Integer(89), new BigDecimal(6.00)));
        assertEquals(3,commission.addCommissionTerm(commissionTerms).getCommissionTerms().size());

    }

}
