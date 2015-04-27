package com.pla.core.domain.service;

import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.model.*;
import com.pla.core.dto.CoverageDto;
import com.pla.core.query.CoverageFinder;
import com.pla.core.specification.CoverageCodeIsUnique;
import com.pla.core.specification.CoverageIsAssociatedWithPlan;
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

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
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
    private CoverageIsAssociatedWithPlan coverageIsAssociatedWithPlan;

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
        CoverageCodeIsUnique coverageCodeIsUnique = new CoverageCodeIsUnique(coverageFinder);
        coverageService = new CoverageService(adminRoleAdapter, coverageNameIsUnique,coverageCodeIsUnique, idGenerator,coverageIsAssociatedWithPlan);
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
        when(coverageNameIsUnique.isSatisfiedBy(anyObject())).thenReturn(true);
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
        String coverageId = "C004";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        CoverageDto coverageDto = new CoverageDto("",name,"C_ONE");
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(coverageNameIsUnique.isSatisfiedBy(coverageDto)).thenReturn(false);
        Coverage coverage = coverageService.createCoverage(name, "coverage description", "C_ONE",benefits, userDetails);
        assertNull(coverage);

    }

    @Test
    public void givenACoverageCode_whenTheCoverageCodeIsUnique_thenItShouldCreateTheCoverageWithActiveState() {
        String coverageId = "C001";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(coverageNameIsUnique.isSatisfiedBy(anyObject())).thenReturn(true);
        Coverage coverage = coverageService.createCoverage(name, "coverage description", "C_ONE",benefits, userDetails);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(coverage, "getCoverageName");
        assertNotNull(coverage);
        assertEquals(new CoverageId(coverageId), invokeGetterMethod(coverage, "getCoverageId"));
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(coverage, "getStatus"));
        assertEquals(name, coverageName.getCoverageName());
    }

    @Test(expected = CoverageException.class)
    public void givenACoverageCode_whenTheCoverageCodeIsNotUnique_thenItShouldThrowTheException() {
        String coverageId = "C001";
        when(idGenerator.nextId()).thenReturn(coverageId);
        String name="testing coverage name";
        CoverageDto coverageDto = new CoverageDto(coverageId,name,"C_ONE");
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(coverageNameIsUnique.isSatisfiedBy(coverageDto)).thenReturn(false);
        Coverage coverage = coverageService.createCoverage(name,"C_ONE","coverage description",benefits, userDetails);
        assertNull(coverage);
    }


    @Test
    public void givenANewCoverageName_whenTheCoverageNameIsUnique_thenItShouldUpdateTheCoverage() {
        String newCoverageName = "new  coverage name";
        Coverage coverage = getCoverage();
        when(coverageNameIsUnique.isSatisfiedBy(anyObject())).thenReturn(true);
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
        String name = "testing coverage name";
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(coverageNameIsUnique.isSatisfiedBy(anyObject())).thenReturn(true);
        Coverage coverage = coverageService.createCoverage(name, "coverage description","C_ONE", benefits, userDetails);
        return coverage;
    }
}
