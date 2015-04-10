package com.pla.core.domain.service;

import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.model.*;
import com.pla.core.query.CoverageFinder;
import com.pla.core.specification.CoverageCodeIsUnique;
import com.pla.core.specification.CoverageNameIsUnique;
import com.pla.sharedkernel.identifier.CoverageId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/*
*
 * Created by Admin on 3/25/2015.

*/


@RunWith(MockitoJUnitRunner.class)
public class CoverageServiceUnitTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private CoverageNameIsUnique coverageNameIsUnique;

    @Mock
    private CoverageCodeIsUnique coverageCodeIsUnique;


    @Mock
    private CoverageFinder coverageFinder;

    @Mock
    private IIdGenerator idGenerator;

    private CoverageService coverageService;

    private UserDetails userDetails;

    private Admin admin;

    private Set<Benefit> benefits=new HashSet<>();
    @Before
    public void setUp() {
        coverageService = new CoverageService(adminRoleAdapter, coverageNameIsUnique,coverageCodeIsUnique, idGenerator);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin();

        String name = "CI Benefit";
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Benefit benefit = admin.createBenefit(true, "B001", name);
        benefits.add(benefit);
    }


    @Test
    public void givenACoverageName_whenTheCoverageNameIsUnique_thenItShouldCreateTheCoverageWithActiveState() {
        String coverageId = "C001";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        String coverageCode="testing coverage name";
        when(coverageNameIsUnique.isSatisfiedBy(new CoverageName(name))).thenReturn(true);
        when(coverageCodeIsUnique.isSatisfiedBy(anyString())).thenReturn(true);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Coverage coverage = coverageService.createCoverage(name, "coverage description",coverageCode,benefits, userDetails);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(coverage, "getCoverageName");
        assertNotNull(coverage);
        assertEquals(new CoverageId(coverageId), invokeGetterMethod(coverage, "getCoverageId"));
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(coverage, "getStatus"));
        assertEquals(name, coverageName.getCoverageName());
    }

    @Test(expected = CoverageException.class)
    public void givenACoverageName_whenTheCoverageNameIsNotUnique_thenItShouldThrowTheException() {
        String coverageId = "C001";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        when(coverageNameIsUnique.isSatisfiedBy(new CoverageName(name))).thenReturn(false);
        when(coverageCodeIsUnique.isSatisfiedBy(anyString())).thenReturn(true);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Coverage coverage = coverageService.createCoverage(name, "coverage description", "C_ONE",benefits, userDetails);
        assertNull(coverage);

    }

    @Test
    public void givenACoverageName_whenTheCoverageCodeIsUnique_thenItShouldCreateTheCoverageWithActiveState() {
        String coverageId = "C001";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        when(coverageNameIsUnique.isSatisfiedBy(new CoverageName(name))).thenReturn(true);
        when(coverageCodeIsUnique.isSatisfiedBy(anyString())).thenReturn(true);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Coverage coverage = coverageService.createCoverage(name, "coverage description", "C_ONE",benefits, userDetails);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(coverage, "getCoverageName");
        assertNotNull(coverage);
        assertEquals(new CoverageId(coverageId), invokeGetterMethod(coverage, "getCoverageId"));
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(coverage, "getStatus"));
        assertEquals(name, coverageName.getCoverageName());
    }

    @Test(expected = CoverageException.class)
    public void givenACoverageName_whenTheCoverageCodeIsNotUnique_thenItShouldThrowTheException() {
        String coverageId = "C001";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        when(coverageNameIsUnique.isSatisfiedBy(new CoverageName(name))).thenReturn(true);
        when(coverageCodeIsUnique.isSatisfiedBy(anyString())).thenReturn(false);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Coverage coverage = coverageService.createCoverage(name, "coverage description", "C_ONE",benefits, userDetails);
        assertNull(coverage);

    }


    @Test
    public void givenANewCoverageName_whenTheCoverageNameIsUnique_thenItShouldUpdateTheCoverage() {
        String newCoverageName = "new  coverage name";
        Coverage coverage = getCoverage();
        when(coverageNameIsUnique.isSatisfiedBy(new CoverageName(newCoverageName))).thenReturn(true);
        when(coverageCodeIsUnique.isSatisfiedBy(anyString())).thenReturn(true);
        Coverage updateCoverage = coverageService.updateCoverage(coverage, newCoverageName,"C_ONE","description", benefits, userDetails);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(updateCoverage, "getCoverageName");
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(updateCoverage, "getStatus"));
        assertEquals(newCoverageName, coverageName.getCoverageName());
    }

    @Test
    public void givenACoverage_whenTheCoverageIsInActiveStatus_thenItShouldMarkTheCoverageAsUsed() {
        Coverage coverage = getCoverage();
        Coverage markCoverageAsUsed = coverageService.markCoverageAsUsed(coverage);
        assertEquals(CoverageStatus.INUSE, invokeGetterMethod(markCoverageAsUsed, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void givenACoverage_whenTheCoverageStatusIsNotActive_thenItShouldThrowAnException() {
        Coverage coverage = getCoverage();
        coverage.deactivate();
        Coverage markCoverageAsUsed = coverageService.markCoverageAsUsed(coverage);
        assertEquals(CoverageStatus.INUSE, invokeGetterMethod(markCoverageAsUsed, "getStatus"));
    }


    @Test
    public void givenACoverage_whenTheCoverageStatusIsActive_thenItShouldInactivateCoverage() {
        Coverage coverage = getCoverage();
        Coverage inactivateCoverage = coverageService.inactivateCoverage(coverage,userDetails);
        assertEquals(CoverageStatus.INACTIVE, invokeGetterMethod(inactivateCoverage, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void givenACoverageToInactivate_whenTheCoverageStatusIsNotActive_thenItShouldThrowAnException() {
        Coverage coverage = getCoverage();
        coverage.markAsUsed();
        Coverage inactivateCoverage = coverageService.inactivateCoverage(coverage,userDetails);
        assertEquals(CoverageStatus.INACTIVE, invokeGetterMethod(inactivateCoverage, "getStatus"));
    }

    private Coverage getCoverage() {
        String name="testing coverage name";
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(coverageNameIsUnique.isSatisfiedBy(new CoverageName(name))).thenReturn(true);
        when(coverageCodeIsUnique.isSatisfiedBy(anyString())).thenReturn(true);
        Coverage coverage = coverageService.createCoverage(name, "coverage description","C_ONE", benefits, userDetails);
        return coverage;
    }
}
