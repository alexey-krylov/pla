/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.CoverageException;
import com.pla.core.dto.BenefitDto;
import com.pla.core.query.BenefitFinder;
import com.pla.core.specification.BenefitIsAssociatedWithCoverage;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * @author: Samir
 * @since 1.0 12/03/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class AdminUnitTest {

    @Mock
    private BenefitNameIsUnique benefitNameIsUnique;

    @Mock
    private BenefitFinder benefitFinder;

    @Mock
    private BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage;

    private Admin admin;

    Set<Benefit> benefitSet = new HashSet<>();

    @Before
    public void setUp() {
        admin = new Admin();
        String name = "CI Benefit";
        boolean isBenefitNameUnique = true;
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        benefitSet.add(benefit);
    }

    @Test
    public void givenABenefitNameItShouldCreateBenefit() {
        String name = "CI Benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = true;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        BenefitName benefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertEquals(name, benefitName.getBenefitName());
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void itShouldInactivateABenefit() {
        String name = "CI Benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = true;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        benefit = admin.inactivateBenefit(benefit);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(benefit, "getStatus"));
    }

    @Test
    public void itShouldUpdateABenefit() {
        String name = "CI Benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = true;
        when(benefitNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        boolean isUpdatable = true;
        when(benefitIsAssociatedWithCoverage.isSatisfiedBy(benefitDto)).thenReturn(isUpdatable);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        String updatedName = "Accidental Benefit";
        Benefit updatedBenefit = admin.updateBenefit(benefit, updatedName, isUpdatable);
        BenefitName updatedBenefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(updatedName, updatedBenefitName.getBenefitName());
    }

    @Test
    public void givenTheCoverageWithSetOfBenefit_whenTheCoverageNameIsUnique_thenItShouldCreateTheCoverage() {
        String name = "CI Benefit";
        boolean isCoverageNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name, "description", benefitSet);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(coverage, "getCoverageName");
        assertEquals(name, coverageName.getCoverageName());
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(coverage, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void givenTheCoverageWithSetOfBenefit_whenTheCoverageNameIsNotUnique_thenItShouldThrowAnException() {
        String name = "coverage name";
        boolean isCoverageNameIsUnique = false;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name, "description", benefitSet);
    }

    @Test
    public void givenTheCoverageWithNewCoverageName_whenCoverageNameIsUnique_thenTheCoverageShouldUpdateWithNewName() {
        String name = "coverage name";
        boolean isCoverageNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name, "description", benefitSet);

        String updatedName = "coverage name after update";
        Coverage updatedCoverage = admin.updateCoverage(coverage,updatedName,"coverage description", benefitSet, true);
        CoverageName updatedCoverageName = (CoverageName) invokeGetterMethod(updatedCoverage, "getCoverageName");
        assertEquals(updatedName, updatedCoverageName.getCoverageName());
    }

    /*
    * Created one coverage with coverage name C_ONE
    * Created another new coverage with coverage name C_TWO
    *
    * When trying t update coverage with coverage name C_TWO to C_ONE
    *
    * then it should throw the exception with message "Coverage name is satisfied"
    *
    * */

    @Test(expected = CoverageException.class)
    public void givenTheCoverageWithNewCoverageName_whenCoverageNameIsNotUnique_thenTheCoverageShouldUpdateWithNewName() {
        String name = "coverage name";
        boolean isCoverageNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name, "description", benefitSet);

        Coverage newCoverage = admin.createCoverage(isCoverageNameIsUnique, "1", "coverage name two", "description", benefitSet);
        String updatedName = "coverage name";
        Coverage updatedCoverage = admin.updateCoverage(newCoverage,updatedName, "coverage description",benefitSet, false);
    }


    @Test
    public void givenACoverage_whenTheCoverageIsInActiveStatus_thenItShouldInActivateTheCoverage() {
        String name = "coverage name";
        boolean isCoverageNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name, "description", benefitSet);
        Coverage inactiveCoverage = admin.inactivateCoverage(coverage);
        assertEquals(CoverageStatus.INACTIVE, invokeGetterMethod(inactiveCoverage, "getStatus"));
    }


    @Test(expected = CoverageException.class)
    public void givenACoverage_whenTheCoverageIsInInUseStatus_thenItShouldThrowAnException() {
        String name = "coverage name";
        boolean isCoverageNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name, "description", benefitSet);
        coverage.markAsUsed();
        Coverage inactiveCoverage = admin.inactivateCoverage(coverage);
    }

    @Test
    public void givenAProductAndAnOptionalCoverage_whenUserHasAdminRole_thenItShouldCreateTheMandatoryDocument(){
        Set<String> documents = new HashSet<>();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        MandatoryDocument mandatoryDocument = admin.createMandatoryDocument("P001", "C001", ProcessType.MATURITY, documents);
        assertEquals(new PlanId("P001"), invokeGetterMethod(mandatoryDocument, "getPlanId"));
        assertEquals(new CoverageId("C001"), invokeGetterMethod(mandatoryDocument, "getCoverageId"));
        assertEquals(ProcessType.MATURITY, invokeGetterMethod(mandatoryDocument, "getProcess"));
        assertEquals(2, mandatoryDocument.getDocuments().size());

    }

    @Test
    public void givenSetOfDocuments_whenUserHasAdminRole_thenItShouldUpdateTheMandatoryDocumentWithTheGivenDocuments() {
        Set<String> documents = new HashSet<>();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        MandatoryDocument mandatoryDocument = admin.createMandatoryDocument("P001", "C001", ProcessType.MATURITY, documents);
        assertEquals(2, mandatoryDocument.getDocuments().size());

        documents = new HashSet<>();
        documents.add("DOCUMENT_THREE");
        documents.add("DOCUMENT_FOUR");
        documents.add("DOCUMENT_FIVE");
        MandatoryDocument updatedMandatoryDocument = admin.updateMandatoryDocument(mandatoryDocument, documents);
        assertEquals(3, updatedMandatoryDocument.getDocuments().size());
    }

}
