package com.pla.core.specification;

import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.query.MandatoryDocumentFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 4/29/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class MandatoryDocumentIsAssociatedWithPlanUnitTest {

    @Mock
    MandatoryDocumentFinder mandatoryDocumentFinder;


    @Test
    public void givenPlanAndProcessType_whenMandatoryDocumentIsNotExistedWithSamePlanAndProcess_thenItShouldReturnTrue(){
        when(mandatoryDocumentFinder.getMandatoryDocumentCountBy(anyString(),anyString(),anyString())).thenReturn(0);
        MandatoryDocumentIsAssociatedWithPlan mandatoryDocumentIsAssociatedWithPlan = new MandatoryDocumentIsAssociatedWithPlan(mandatoryDocumentFinder);
        MandatoryDocumentDto mandatoryDocumentDto = new MandatoryDocumentDto();
        boolean isMandatoryDocumentIsAssociatedWithPlan = mandatoryDocumentIsAssociatedWithPlan.isSatisfiedBy(mandatoryDocumentDto);
        assertTrue(isMandatoryDocumentIsAssociatedWithPlan);
    }


    @Test
    public void givenPlanAndProcessType_whenMandatoryDocumentIsExistedWithSamePlanAndProcess_thenItShouldReturnFalse(){
        when(mandatoryDocumentFinder.getMandatoryDocumentCountBy(anyString(),anyString(),anyString())).thenReturn(1);
        MandatoryDocumentIsAssociatedWithPlan mandatoryDocumentIsAssociatedWithPlan = new MandatoryDocumentIsAssociatedWithPlan(mandatoryDocumentFinder);
        MandatoryDocumentDto mandatoryDocumentDto = new MandatoryDocumentDto();
        boolean isMandatoryDocumentIsAssociatedWithPlan = mandatoryDocumentIsAssociatedWithPlan.isSatisfiedBy(mandatoryDocumentDto);
        assertFalse(isMandatoryDocumentIsAssociatedWithPlan);
    }
}
