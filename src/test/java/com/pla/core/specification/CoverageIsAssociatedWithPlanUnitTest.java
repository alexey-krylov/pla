package com.pla.core.specification;

import com.google.common.collect.Lists;
import com.pla.core.dto.CoverageDto;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.identifier.CoverageId;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 4/27/2015.
 */
public class CoverageIsAssociatedWithPlanUnitTest {

    @Test
     public void givenCoverage_whenCoverageIsAssociatedWithPlan_thenItShouldReturnTrue(){
        PlanFinder planFinder = mock(PlanFinder.class);
        when(planFinder.getAllCoverageAssociatedWithPlan()).thenReturn(Lists.newArrayList(new CoverageId("C001"),new CoverageId("C002")));
        CoverageIsAssociatedWithPlan coverageIsAssociatedWithPlan = new CoverageIsAssociatedWithPlan(planFinder);
        boolean isCoverageAssociatedWithPlan = coverageIsAssociatedWithPlan.isSatisfiedBy(new CoverageDto("C001","Testing Coverage","C_one"));
        assertTrue(isCoverageAssociatedWithPlan);
    }

    @Test
    public void givenCoverage_whenCoverageIsNotAssociatedWithPlan_thenItShouldReturnFalse(){
        PlanFinder planFinder = mock(PlanFinder.class);
        when(planFinder.getAllCoverageAssociatedWithPlan()).thenReturn(Lists.newArrayList(new CoverageId("C001"),new CoverageId("C002")));
        CoverageIsAssociatedWithPlan coverageIsAssociatedWithPlan = new CoverageIsAssociatedWithPlan(planFinder);
        boolean isCoverageAssociatedWithPlan = coverageIsAssociatedWithPlan.isSatisfiedBy(new CoverageDto("C004","Testing Coverage","C_one"));
        assertFalse(isCoverageAssociatedWithPlan);
    }
}
