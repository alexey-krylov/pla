package com.pla.core.specification;

import com.pla.core.domain.model.CoverageName;
import com.pla.core.dto.BenefitDto;
import com.pla.core.query.CoverageFinder;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
        assertThat(true, is(alreadyExists));
    }


}

