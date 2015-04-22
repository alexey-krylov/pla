package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.core.dto.PolicyProcessMinimumLimitItemDto;
import com.pla.sharedkernel.domain.model.PolicyFeeProcessType;
import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 4/6/2015.
 */
public class ProductLineInformationUnitTest {

    List<Map<ProductLineProcessType,Integer>> listOfProcessItems = Lists.newArrayList();
    Map<ProductLineProcessType,Integer> productLineProcessItemMap;

    @Before
    public void setUp(){
        productLineProcessItemMap = Maps.newLinkedHashMap();
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

    }

    @Test
    public void givenEnrollmentProcessInformation_thenItShouldAddTheProcessItemToProductLineInformation(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);

        Set<ProductLineProcessItem> enrollmentProcessItems =  productLineGeneralInformation.getEnrollmentProcessInformation().getEnrollmentProcessItems();
        assertNotNull(productLineGeneralInformation.getEnrollmentProcessInformation());
        assertThat(enrollmentProcessItems.size(),is(5));

    }

    @Test
      public void givenEndorsementProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withEndorsementProcessInformation(listOfProcessItems);

        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getEndorsementProcessInformation().getEndorsementProcessItems();
        assertNotNull(productLineGeneralInformation.getEndorsementProcessInformation());
        assertThat(productLineProcessItems.size(), is(5));
    }

    @Test
    public void givenReinstatementProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withReinstatementProcessInformation(listOfProcessItems);

        assertNotNull(productLineGeneralInformation.getReinstatementProcessInformation());
        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getReinstatementProcessInformation().getReinstatementProcessItems();
        assertThat(productLineProcessItems.size(), is(5));

    }

    @Test
    public void givenQuotationProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){

        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);

        assertNotNull(productLineGeneralInformation.getQuotationProcessInformation());
        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getQuotationProcessInformation().getQuotationProcessItems();
        assertThat(productLineProcessItems.size(), is(5));

    }

    @Test(expected = GeneralInformationException.class)
    public void givenQuotationProcessInformation_whenQuotationProcessHasEarlyDeathCriteriaType_thenItShouldThrowAnGenericInformationException(){

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.EARLY_DEATH_CRITERIA, 20);
        listOfProcessItems.add(productLineProcessItemMap);

        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);

        assertNotNull(productLineGeneralInformation.getQuotationProcessInformation());
        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getQuotationProcessInformation().getQuotationProcessItems();
        assertThat(productLineProcessItems.size(), is(5));

    }

    @Test
    public void givenClaimProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withClaimProcessInformation(listOfProcessItems);

        assertNotNull(productLineGeneralInformation.getClaimProcessInformation());
        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getClaimProcessInformation().getClaimProcessItems();
        assertThat(productLineProcessItems.size(), is(5));

    }

    @Test
    public void givenPolicyFeeProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){

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

        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withPolicyFeeProcessInformation(policyFeeProcessInformation);

        Set<PolicyFeeProcessItem> productLineProcessItems = productLineGeneralInformation.getPolicyFeeProcessInformation().getPolicyFeeProcessItems();
        assertNotNull(productLineGeneralInformation.getPolicyFeeProcessInformation());
        assertThat(productLineProcessItems.size(), is(4));

    }

    @Test
    public void givenPolicyProcessMinimumLimitInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){

        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimit =Lists.newArrayList();
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

        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withPolicyProcessMinimumLimit(policyProcessMinimumLimit);

        Set<PolicyProcessMinimumLimitItem> productLineProcessItems = productLineGeneralInformation.getPolicyProcessMinimumLimit().getPolicyProcessMinimumLimitItems();
        assertNotNull(productLineGeneralInformation.getPolicyProcessMinimumLimit());
        assertThat(productLineProcessItems.size(), is(2));
    }

    /*
    *
    * Add the check for line of business id
    * */
    /*@Test(expected = IllegalArgumentException.class)
    public void givenPolicyProcessMinimumLimitInformation_whenLineOfBusinessIdIsOtherThenGroupHealthOrGroupInsurance_thenItShouldThrowAnException(){

        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimit =Lists.newArrayList();
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

        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.INDIVIDUAL_INSURANCE);
        productLineGeneralInformation = productLineGeneralInformation.withPolicyProcessMinimumLimit(policyProcessMinimumLimit);


    }*/


    @Test
    public void givenSurrenderProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withSurrenderProcessInformation(listOfProcessItems);

        assertNotNull(productLineGeneralInformation.getSurrenderProcessInformation());
        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getSurrenderProcessInformation().getSurrenderProcessItems();
        assertThat(productLineProcessItems.size(), is(5));

    }

    @Test
    public void givenMaturityProcessInformation_thenItShouldAddTheProcessTypesToProductLineInformation(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withMaturityProcessInformation(listOfProcessItems);

        assertNotNull(productLineGeneralInformation.getMaturityProcessInformation());
        Set<ProductLineProcessItem> productLineProcessItems = productLineGeneralInformation.getMaturityProcessInformation().getMaturityProcessItems();
        assertThat(productLineProcessItems.size(), is(5));

    }

}
