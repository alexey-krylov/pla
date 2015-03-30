package com.pla.core.domain.model;

import com.pla.core.domain.exception.CoverageException;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * Created by Admin on 3/25/2015.
 */
public class CoverageUnitTest {

    private Benefit benefit;
    private Coverage coverage;
    private Admin admin;
    Set<Benefit> benefitSet=new HashSet<>();
    String name = "testing coverage name";
    @Before
    public void setUp(){
        admin = new Admin();
        boolean isBenefitNameUnique = true;
        benefit = admin.createBenefit(isBenefitNameUnique, "B001", name);
        benefitSet.add(benefit);
        coverage = admin.createCoverage(isBenefitNameUnique, "C001", name, "coverage description",benefitSet);
    }

    @Test
    public void givenCoverageNameAndSetOfBenefits_whenCoverageGotCreated_thenTheCoverageStatusShouldBeActive(){
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(coverage, "getStatus"));
    }

    @Test
    public void givenCoverageWithActiveStatus_whenTheCoverageMarkedAsUsed_thenTheCoverageStatusShouldChangeFromActiveToInUse(){
        coverage = coverage.markAsUsed();
        assertEquals(CoverageStatus.INUSE, invokeGetterMethod(coverage, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void  givenCoverageWithInActiveStatus_whenUserTryingToMarkTheAsUsed_thenItShouldThrowAnException(){
        coverage = admin.inactivateCoverage(coverage);
        coverage = coverage.markAsUsed();
        assertEquals(CoverageStatus.INACTIVE, invokeGetterMethod(coverage, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void givenCoverageWithInUseStatus__whenUseTryingToInActivateTheInUseCoverage_thenItShouldThrowAnException(){
        coverage = coverage.markAsUsed();
        coverage = admin.inactivateCoverage(coverage);
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(coverage, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void givenCoverageWithInUseStatus__whenUseTryingToUpdateTheCoverageName_thenItShouldThrowAnException(){
        coverage = coverage.markAsUsed();
        String updatedName = "Updating coverage name";
        coverage = coverage.updateCoverageName(updatedName);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(coverage, "getCoverageName");
        assertEquals(name, coverageName.getCoverageName());
    }

    @Test
    public void benefitInActiveStatusShouldGetUpdatedWithNewName() {
        String updatedName = "Updating coverage name";
        Coverage updateCoverageName = coverage.updateCoverageName(updatedName);;
        CoverageName coverageName = (CoverageName) invokeGetterMethod(updateCoverageName, "getCoverageName");
        assertEquals(updatedName, coverageName.getCoverageName());
    }
}
