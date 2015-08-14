package com.pla.core.specification;

import com.pla.core.dto.CoverageDto;
import com.pla.core.query.CoverageFinder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 4/27/2015.
 */
public class CoverageCodeIsUniqueUnitTest {

    @Test
    public void givenCoverage_whenCoverageCodeIsUnique_thenItShouldReturnTrue(){
        CoverageFinder coverageFinder = mock(CoverageFinder.class);
        when(coverageFinder.getCoverageCountByCoverageCode("C_ONE","C001")).thenReturn(0);
        CoverageCodeIsUnique coverageCodeIsUnique = new CoverageCodeIsUnique(coverageFinder);
        boolean isCoverageCodeIsUnique = coverageCodeIsUnique.isSatisfiedBy(new CoverageDto("C001","Testing Coverage","C_ONE"));
        assertTrue(isCoverageCodeIsUnique);
    }

    @Test
    public void givenCoverage_whenCoverageCodeIsNotUnique_thenItShouldReturnFalse(){
        CoverageFinder coverageFinder = mock(CoverageFinder.class);
        when(coverageFinder.getCoverageCountByCoverageCode("C_ONE","C001")).thenReturn(1);
        CoverageCodeIsUnique coverageCodeIsUnique = new CoverageCodeIsUnique(coverageFinder);
        boolean isCoverageCodeIsUnique = coverageCodeIsUnique.isSatisfiedBy(new CoverageDto("C001","Testing Coverage","C_ONE"));
        assertFalse(isCoverageCodeIsUnique);
    }
}
