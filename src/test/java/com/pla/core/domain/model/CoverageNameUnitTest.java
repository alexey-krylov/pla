package com.pla.core.domain.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 4/24/2015.
 */
public class CoverageNameUnitTest {

    @Test
    public void givenACoverageName_whenCoverageNameIsNotNull_thenItShouldCreateTheCoverageName(){
        CoverageName coverageName = new CoverageName("Health coverage");
        assertNotNull(coverageName);
        assertThat("Health coverage",is(coverageName.getCoverageName()));
    }

    @Test(expected = NullPointerException.class)
    public void givenACoverageName_whenCoverageNameIsNull_thenItShouldThrowAnExcpetion(){
        CoverageName coverageName = new CoverageName(null);
        assertNull(coverageName);
    }
}
