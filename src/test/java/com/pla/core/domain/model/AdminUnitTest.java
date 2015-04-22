/*
 * Copyright (c) 3/12/15 2:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */


package com.pla.core.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.core.dto.BenefitDto;
import com.pla.core.dto.GeneralInformationDto;
import com.pla.core.dto.PolicyProcessMinimumLimitItemDto;
import com.pla.core.query.BenefitFinder;
import com.pla.core.specification.BenefitIsAssociatedWithCoverage;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
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

    GeneralInformationDto generalInformationDto = new GeneralInformationDto();
    List<Map<ProductLineProcessType,Integer>> listOfProcessItems;

    @Before
    public void setUp() {
        admin = new Admin();
        String name = "CI Benefit";
        boolean isBenefitNameUnique = true;
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, "1", name);
        benefitSet.add(benefit);

        listOfProcessItems = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.PURGE_TIME_PERIOD, 10);
        listOfProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.NO_OF_REMAINDER, 11);
        listOfProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.FIRST_REMAINDER,12);
        listOfProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.GAP, 13);
        listOfProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.CLOSURE, 14);
        listOfProcessItems.add(productLineProcessItemMap);

        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessInformation =Lists.newArrayList();
        Map<PolicyFeeProcessType,Integer> policyFeeProcessTypeMap = Maps.newLinkedHashMap();
        policyFeeProcessTypeMap.put(PolicyFeeProcessType.ANNUAL,20);
        policyFeeProcessInformation.add(policyFeeProcessTypeMap);

        policyFeeProcessTypeMap = Maps.newLinkedHashMap();
        policyFeeProcessTypeMap.put(PolicyFeeProcessType.SEMI_ANNUAL,17);
        policyFeeProcessInformation.add(policyFeeProcessTypeMap);

        policyFeeProcessTypeMap = Maps.newLinkedHashMap();
        policyFeeProcessTypeMap.put(PolicyFeeProcessType.QUARTERLY,18);
        policyFeeProcessInformation.add(policyFeeProcessTypeMap);

        policyFeeProcessTypeMap = Maps.newLinkedHashMap();
        policyFeeProcessTypeMap.put(PolicyFeeProcessType.MONTHLY,19);
        policyFeeProcessInformation.add(policyFeeProcessTypeMap);

        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimit = Lists.newArrayList();
        PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.ANNUAL);
        policyProcessMinimumLimitItemDto.setNoOfPersonPerPolicy(10);
        policyProcessMinimumLimitItemDto.setMinimumPremium(2);
        policyProcessMinimumLimit.add(policyProcessMinimumLimitItemDto);

        policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.SEMI_ANNUAL);
        policyProcessMinimumLimitItemDto.setNoOfPersonPerPolicy(20);
        policyProcessMinimumLimitItemDto.setMinimumPremium(8);
        policyProcessMinimumLimit.add(policyProcessMinimumLimitItemDto);
        generalInformationDto.setPolicyProcessMinimumLimitItems(policyProcessMinimumLimit);
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
        benefit = admin.inactivateBenefit(benefit,true);
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
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name,"C_ONE", "description", benefitSet);
        CoverageName coverageName = (CoverageName) invokeGetterMethod(coverage, "getCoverageName");
        assertEquals(name, coverageName.getCoverageName());
        assertEquals(CoverageStatus.ACTIVE, invokeGetterMethod(coverage, "getStatus"));
    }

    @Test(expected = CoverageException.class)
    public void givenTheCoverageWithSetOfBenefit_whenTheCoverageNameIsNotUnique_thenItShouldThrowAnException() {
        String name = "coverage name";
        boolean isCoverageNameIsUnique = false;
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameIsUnique, "1", name,"C_ONE", "description", benefitSet);
    }

    @Test(expected = CoverageException.class)
    public void givenTheCoverageWithSetOfBenefit_whenTheCoverageCodeIsNotUnique_thenItShouldThrowAnException() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = false;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name,"C_ONE", "description", benefitSet);
    }

    @Test
    public void givenTheCoverageWithNewCoverageName_whenCoverageNameIsUnique_thenTheCoverageShouldUpdateWithNewName() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name, "C_ONE","description", benefitSet);

        String updatedName = "coverage name after update";
        Coverage updatedCoverage = admin.updateCoverage(coverage,updatedName,"C_TWO","coverage description", benefitSet, true);
        CoverageName updatedCoverageName = (CoverageName) invokeGetterMethod(updatedCoverage, "getCoverageName");
        assertEquals(updatedName, updatedCoverageName.getCoverageName());
    }

    @Test
    public void givenTheCoverageWithNewCoverageCode_whenCoverageCodeIsUnique_thenTheCoverageShouldUpdateWithNewCoverageCode() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name, "C_ONE","description", benefitSet);

        String updatedName = "coverage name after update";
        String newCoverageCode = "coverage name after update";
        Coverage updatedCoverage = admin.updateCoverage(coverage,updatedName,newCoverageCode,"coverage description", benefitSet, true);
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
    public void givenTheCoverageWithCoverageName_whenCoverageNameIsNotUnique_thenTheCoverageShouldNotUpdateWithNewName() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name,"C_ONE", "description", benefitSet);

        Coverage newCoverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", "coverage name two","C_ONE", "description", benefitSet);
        String updatedName = "coverage name";
        Coverage updatedCoverage = admin.updateCoverage(newCoverage,updatedName,"C_TWO", "coverage description",benefitSet, false);
    }

    @Test(expected = CoverageException.class)
    public void givenTheCoverageWithCoverageCode_whenCoverageCodeIsNotUnique_thenTheCoverageShouldNotUpdateWithNewCoverageCode() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name,"C_ONE", "description", benefitSet);

        Coverage newCoverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", "coverage name two","C_TWO", "description", benefitSet);
        String updatedName = "new coverage name";
        Coverage updatedCoverage = admin.updateCoverage(newCoverage,updatedName,"C_ONE", "coverage description",benefitSet,false);
    }


    @Test
    public void givenACoverage_whenTheCoverageIsInActiveStatus_thenItShouldInActivateTheCoverage() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name,"C_ONE", "description", benefitSet);
        Coverage inactiveCoverage = admin.inactivateCoverage(coverage);
        assertEquals(CoverageStatus.INACTIVE, invokeGetterMethod(inactiveCoverage, "getStatus"));
    }


    @Test(expected = CoverageException.class)
    public void givenACoverage_whenTheCoverageIsInInUseStatus_thenItShouldThrowAnException() {
        String name = "coverage name";
        boolean isCoverageNameOrNameIsUnique = true;
        Coverage coverage = admin.createCoverage(isCoverageNameOrNameIsUnique, "1", name,"C_ONE", "description", benefitSet);
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


/*
    * Given
    *    the general information for the
    *       1.Group life level
    *       2.Group health level and
    *       3.Individual information
    * When
    *    all the information are given i.e, all the information provided
    * Then
    *    the product line information should get created with all the process information
    *
    * */

    @Test
    public void givenLineOfBusinessIdAndGeneralInformation_thenItShouldCreateTheProductLineInformationWithTheGivenProcess(){
        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItems = Lists.newArrayList();
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessItems = Lists.newArrayList();
        ProductLineGeneralInformation createdProductLineGeneralInformation = admin.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH, listOfProcessItems,listOfProcessItems,listOfProcessItems,listOfProcessItems,listOfProcessItems,policyFeeProcessItems,policyProcessMinimumLimitItems,listOfProcessItems,listOfProcessItems);
        assertNotNull(createdProductLineGeneralInformation);
        assertNotNull(createdProductLineGeneralInformation.getQuotationProcessInformation());
        assertNotNull(createdProductLineGeneralInformation.getEnrollmentProcessInformation());
        assertNotNull(createdProductLineGeneralInformation.getEndorsementProcessInformation());
        assertNotNull(createdProductLineGeneralInformation.getReinstatementProcessInformation());
        assertNotNull(createdProductLineGeneralInformation.getClaimProcessInformation());
        assertNotNull(createdProductLineGeneralInformation.getSurrenderProcessInformation());
        assertNotNull(createdProductLineGeneralInformation.getMaturityProcessInformation());
    }


/*
    * Given
    *    the line of business id,general information
    * When
    *    the general information has Early Death Criteria product line process type is associated with the process other than Claim process
    * Then
    *    the Generic exception should be thrown with message "Early Death Criteria is applicable only for claim request"
    * */

    @Test(expected = GeneralInformationException.class)
    public void givenLineOfBusinessIdAndGeneralInformation_whenEarlyDeathCriteriaIsAssociatedWithOtherThanClaimProcess_thenItThrowAnGenericInformationException(){
        Map<ProductLineProcessType,Integer> productLineProcessItemMap = Maps.newLinkedHashMap();
        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItems = Lists.newArrayList();
        productLineProcessItemMap.put(ProductLineProcessType.EARLY_DEATH_CRITERIA, 10);
        listOfProcessItems.add(productLineProcessItemMap);
        ProductLineGeneralInformation createdProductLineGeneralInformation = admin.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH, listOfProcessItems,listOfProcessItems,listOfProcessItems,listOfProcessItems,listOfProcessItems,null,policyProcessMinimumLimitItems,listOfProcessItems,listOfProcessItems);
        assertNull(createdProductLineGeneralInformation);
    }


/*
   * Given
   *    the line of business id,general information
   * When
   *    the line of business id is Individual information for minimum limit request
   * Then
   *    the exception should be thrown as product line general information
   * */

    /*
    *
    * Add the line of business id check for minimum limit process
    * */
/*
    @Test(expected = IllegalArgumentException.class)
    public void givenLineOfBusinessIdAndGeneralInformation_whenLineOfBusinessIdIsIndividualInsurance_thenItThrowAnException(){
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessItems = Lists.newArrayList();
        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItems = Lists.newArrayList();
        ProductLineGeneralInformation createdProductLineGeneralInformation = admin.createProductLineGeneralInformation(LineOfBusinessId.INDIVIDUAL_INSURANCE, listOfProcessItems,listOfProcessItems,listOfProcessItems,listOfProcessItems,listOfProcessItems,policyFeeProcessItems,policyProcessMinimumLimitItems,listOfProcessItems,listOfProcessItems);
        assertNull(createdProductLineGeneralInformation);
    }
*/



/*
    * Given
    *    the service tax, modal factor and discount factor information
    *  When
    *   all the mandatory information are provided
    * Then
    *    it should create the Organization general information with discount and modal factor values limited to 4 decimal places
    *    and service tax value limited 3 digits and to 2 decimal places
    * */

    @Test
    public void givenServiceTaxDiscountAndModalFactorInformation_thenItShouldCreateTheOrganizationInformation(){
        Map<Tax,BigDecimal> serviceTaxMap = Maps.newLinkedHashMap();
        serviceTaxMap.put(Tax.SERVICE_TAX, new BigDecimal(123.9567));

        List<Map<DiscountFactorItem,BigDecimal>> listOfDiscountFactorItem  = Lists.newArrayList();
        Map<DiscountFactorItem,BigDecimal> discountFactorMap = Maps.newLinkedHashMap();
        discountFactorMap.put(DiscountFactorItem.QUARTERLY, new BigDecimal(114.123449));
        listOfDiscountFactorItem.add(discountFactorMap);
        discountFactorMap = Maps.newLinkedHashMap();
        discountFactorMap.put(DiscountFactorItem.ANNUAL, new BigDecimal(111.123441));
        listOfDiscountFactorItem.add(discountFactorMap);
        discountFactorMap = Maps.newLinkedHashMap();
        discountFactorMap.put(DiscountFactorItem.SEMI_ANNUAL, new BigDecimal(112.123445));
        listOfDiscountFactorItem.add(discountFactorMap);

        List<Map<ModalFactorItem,BigDecimal>> listOfModalFactorItem  = Lists.newArrayList();
        Map<ModalFactorItem,BigDecimal> modalFactorMap = Maps.newLinkedHashMap();
        modalFactorMap.put(ModalFactorItem.SEMI_ANNUAL, new BigDecimal(100.188446));
        listOfModalFactorItem.add(modalFactorMap);

        modalFactorMap = Maps.newLinkedHashMap();
        modalFactorMap.put(ModalFactorItem.QUARTERLY, new BigDecimal(101.123448));
        listOfModalFactorItem.add(modalFactorMap);

        modalFactorMap = Maps.newLinkedHashMap();
        modalFactorMap.put(ModalFactorItem.MONTHLY, new BigDecimal(102.123449));
        listOfModalFactorItem.add(modalFactorMap);

        OrganizationGeneralInformation organizationGeneralInformation = admin.createOrganizationGeneralInformation(listOfModalFactorItem, listOfDiscountFactorItem, serviceTaxMap);
        assertNotNull(organizationGeneralInformation);
        assertNotNull(organizationGeneralInformation.getServiceTax());
        assertNotNull(organizationGeneralInformation.getDiscountFactorItems());
        assertNotNull(organizationGeneralInformation.getModelFactorItems());

    }
}

