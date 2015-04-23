package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.exception.GeneralInformationException;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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

        List<Map<PolicyProcessMinimumLimitType,Integer>> policyProcessMinimumLimit =Lists.newArrayList();
        Map<PolicyProcessMinimumLimitType,Integer> policyProcessMinimumLimitMap = Maps.newLinkedHashMap();
        policyProcessMinimumLimitMap.put(PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY,10);
        policyProcessMinimumLimit.add(policyProcessMinimumLimitMap);
        policyProcessMinimumLimitMap = Maps.newLinkedHashMap();
        policyProcessMinimumLimitMap.put(PolicyProcessMinimumLimitType.MINIMUM_PREMIUM, 10);
        policyProcessMinimumLimit.add(policyProcessMinimumLimitMap);

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
    @Test
    public void givenPolicyProcessMinimumLimitInformation_whenLineOfBusinessIdIsOtherThenGroupHealthOrGroupInsurance_thenItShouldThrowAnException(){

        List<Map<PolicyProcessMinimumLimitType,Integer>> policyProcessMinimumLimit =Lists.newArrayList();
        Map<PolicyProcessMinimumLimitType,Integer> policyProcessMinimumLimitMap = Maps.newLinkedHashMap();
        policyProcessMinimumLimitMap.put(PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY, 10);
        policyProcessMinimumLimit.add(policyProcessMinimumLimitMap);
        policyProcessMinimumLimitMap = Maps.newLinkedHashMap();
        policyProcessMinimumLimitMap.put(PolicyProcessMinimumLimitType.MINIMUM_PREMIUM, 10);
        policyProcessMinimumLimit.add(policyProcessMinimumLimitMap);


        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessId.INDIVIDUAL_INSURANCE);
        productLineGeneralInformation = productLineGeneralInformation.withPolicyProcessMinimumLimit(policyProcessMinimumLimit);

    }


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

    @Test
    public void givenReinstatementProcessInformation_thenItShouldReturnTheReinstatementProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        ReinstatementProcessInformation reinstatementProcessInformation = ReinstatementProcessInformation.create(reinstatementProcessItems);
        assertNotNull(reinstatementProcessInformation);
    }

    @Test
    public void givenQuotationProcessInformation_thenItShouldReturnTheQuotationProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> quotationProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        QuotationProcessInformation quotationProcessInformation = QuotationProcessInformation.create(quotationProcessItems);
        assertNotNull(quotationProcessInformation);
    }

    @Test
    public void givenEnrollmentProcessInformation_thenItShouldReturnTheEnrollmentProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        EnrollmentProcessInformation enrollmentProcessInformation = EnrollmentProcessInformation.create(enrollmentProcessItems);
        assertNotNull(enrollmentProcessInformation);
    }

    @Test
    public void givenEndorsementProcessInformation_thenItShouldReturnTheEndorsementProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        EndorsementProcessInformation endorsementProcessInformation = EndorsementProcessInformation.create(endorsementProcessItems);
        assertNotNull(endorsementProcessInformation);
    }

    @Test
    public void givenClaimProcessInformation_thenItShouldReturnTheClaimProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> claimProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        ClaimProcessInformation claimProcessInformation = ClaimProcessInformation.create(claimProcessItems);
        assertNotNull(claimProcessInformation);
    }

    @Test
    public void givenSurrenderProcessInformation_thenItShouldReturnTheSurrenderProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        SurrenderProcessInformation surrenderProcessInformation = SurrenderProcessInformation.create(surrenderProcessItems);
        assertNotNull(surrenderProcessInformation);
    }

    @Test
    public void givenMaturityProcessInformation_thenItShouldReturnTheMaturityProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> maturityProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        MaturityProcessInformation maturityProcessInformation = MaturityProcessInformation.create(maturityProcessItems);
        assertNotNull(maturityProcessInformation);
    }

    @Test
    public void givenPolicyFeeProcessInformation_thenItShouldReturnThePolicyFeeProcessInformation(){
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessItems  = Lists.newArrayList();
        Map<PolicyFeeProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(PolicyFeeProcessType.MONTHLY,12);
        PolicyFeeProcessInformation policyFeeProcessInformation = PolicyFeeProcessInformation.create(policyFeeProcessItems);
        assertNotNull(policyFeeProcessInformation);
    }
    @Test
    public void givenMinimumLimitProcessInformation_thenItShouldReturnTheMinimumLimitProcessInformation(){
        List<Map<PolicyProcessMinimumLimitType,Integer>> policyMinimumLimitProcessItems  = Lists.newArrayList();
        Map<PolicyProcessMinimumLimitType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(PolicyProcessMinimumLimitType.MINIMUM_PREMIUM,12);
        PolicyProcessMinimumLimit policyProcessMinimumLimit = PolicyProcessMinimumLimit.create(policyMinimumLimitProcessItems);
        assertNotNull(policyProcessMinimumLimit);
    }

}
