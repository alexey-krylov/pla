package com.pla.core.specification;

import com.pla.core.dto.CoverageDto;
import com.pla.core.query.CoverageFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 3/25/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CoverageNameIsUniqueTest {

    @Mock
    CoverageFinder coverageFinder;

    @Test
    public void givenACoverageName_whenTheCoverageNameIsUnique_thenItShouldReturnAsTrue() {
        when(coverageFinder.getCoverageCountByCoverageName("find Coverage Name","C_ONE")).thenReturn(0);
        CoverageNameIsUnique coverageNameIsUnique = new CoverageNameIsUnique(coverageFinder);
        CoverageDto coverageDto = new CoverageDto("C001","find Coverage Name","C_ONE");
        boolean alreadyExists = coverageNameIsUnique.isSatisfiedBy(coverageDto);
        assertTrue(alreadyExists);
    }

    @Test
     public void givenACoverageName_whenTheCoverageNameIsUnique_thenItShouldReturnAsFalse() {
        when(coverageFinder.getCoverageCountByCoverageName(anyString(),anyString())).thenReturn(1);
        CoverageNameIsUnique coverageNameIsUnique = new CoverageNameIsUnique(coverageFinder);
        CoverageDto coverageDto = new CoverageDto("C001","another Coverage Name","C_ONE");
        boolean alreadyExists = coverageNameIsUnique.isSatisfiedBy(coverageDto);
        assertFalse(alreadyExists);
    }
}

