package com.pla.core.specification;

import com.pla.core.domain.model.CoverageName;
import com.pla.core.query.CoverageFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        when(coverageFinder.getCoverageCountByCoverageName("find Coverage Name")).thenReturn(0);
        CoverageNameIsUnique coverageNameIsUnique = new CoverageNameIsUnique(coverageFinder);
        CoverageName coverageName = new CoverageName("find Coverage Name");
        boolean alreadyExists = coverageNameIsUnique.isSatisfiedBy(coverageName);
        assertTrue(alreadyExists);
    }

    @Test
    public void givenACoverageName_whenTheCoverageNameIsUnique_thenItShouldReturnAsFalse() {
        when(coverageFinder.getCoverageCountByCoverageName("another Coverage Name")).thenReturn(1);
        CoverageNameIsUnique coverageNameIsUnique = new CoverageNameIsUnique(coverageFinder);
        CoverageName coverageName = new CoverageName("another Coverage Name");
        boolean alreadyExists = coverageNameIsUnique.isSatisfiedBy(coverageName);
        assertFalse(alreadyExists);
    }
    }

