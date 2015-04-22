/*
 * Copyright (c) 3/10/15 3:31 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.dto.BenefitDto;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import com.pla.sharedkernel.identifier.BenefitId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class BenefitUnitTest {

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    private Benefit benefit;

    private Admin admin;

    private String name = "Accidental death benefit";

    @Before
    public void setUp() {
        admin = new Admin();
        boolean isBenefitNameUnique = true;
        String name = "Accidental death benefit";
        BenefitDto benefitDto = new BenefitDto("1","Accidental death benefit") ;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
    }

    @Test
    public void statusShouldBeActiveOnCreateOfNewBenefit() {
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void statusOfBenefitShouldBeInUse() {
        benefit = benefit.markAsUsed();
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(benefit, "getStatus"));
    }


    @Test(expected = BenefitDomainException.class)
    public void markingAnInactivatedBenefitShouldThrowExceptionAndStatusShouldBeInactive() {
        benefit = admin.inactivateBenefit(benefit,true);
        benefit = benefit.markAsUsed();
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test(expected = BenefitDomainException.class)
    public void inactivatingABenefitWithInUseStatusThrowExceptionAndStatusShouldBeInUse() {
        benefit = benefit.markAsUsed();
        benefit = admin.inactivateBenefit(benefit,true);
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test(expected = BenefitDomainException.class)
    public void benefitInUsedStatusShouldThrowExceptionOnUpdatingNameAndNameShouldBeUnchanged() {
        benefit = benefit.markAsUsed();
        String updatedName = "CI Benefit";
        benefit = benefit.updateBenefitName(new BenefitName(updatedName));
        BenefitName benefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(name, benefitName.getBenefitName());
    }

    @Test
    public void benefitInActiveStatusShouldGetUpdatedWithNewName() {
        String updatedName = "CI Benefit";
        Benefit updatedBenefit = benefit.updateBenefitName(new BenefitName(updatedName));
        BenefitName benefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(updatedName, benefitName.getBenefitName());
    }
    @Test
    public void givenBenefitIdAndName_whenIdAndNameAreNotNull_thenItShouldCreateTheBenefit(){
        Benefit benefit = new Benefit(new BenefitId("B001"),new BenefitName("Health benefit"),BenefitStatus.ACTIVE);
        assertNotNull(benefit);
        assertEquals(new BenefitName("Health benefit"), invokeGetterMethod(benefit, "getBenefitName"));
    }

    @Test(expected = NullPointerException.class)
    public void givenBenefitIdAndName_whenIdAndNameAAreNull_thenItShouldThrowTheException(){
        Benefit benefit = new Benefit(null,new BenefitName("Health benefit"),BenefitStatus.ACTIVE);
        assertNotNull(benefit);
        assertEquals(new BenefitName("Health benefit"), invokeGetterMethod(benefit, "getBenefitName"));
    }
}
