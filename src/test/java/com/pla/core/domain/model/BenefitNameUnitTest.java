package com.pla.core.domain.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/24/2015.
 */
public class BenefitNameUnitTest {

    @Test
    public void givenABenefitName_whenBenefitNameIsNotNull_thenItShouldCreateTheBenefitName(){
        BenefitName benefitName = new BenefitName("B001");
        assertThat(new BenefitName("B001"), is(benefitName));
    }

    @Test
    public void givenABenefitName_thenItShouldCreateTheBenefitName(){
        String newBenefitName = "B002";
        BenefitName benefitName = new BenefitName("B001");
        assertNotEquals(newBenefitName, is(benefitName.getBenefitName()));
    }
}
