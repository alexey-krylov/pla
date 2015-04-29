package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
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
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

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
        productLineProcessItemMap.put(ProductLineProcessType.SECOND_REMAINDER, 11);
        listOfProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.FIRST_REMAINDER,12);
        listOfProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.LAPSE, 13);
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
        reinstatementProcessItems.add(processItem);
        ReinstatementProcessInformation reinstatementProcessInformation = ReinstatementProcessInformation.create(reinstatementProcessItems);
        assertNotNull(reinstatementProcessInformation);
    }

    @Test(expected = GeneralInformationException.class)
    public void givenReinstatementProcessInformation_whenTheProcessTypeIsEarlyDeathCriteria_thenItShouldThrowAnException(){
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.EARLY_DEATH_CRITERIA,12);
        reinstatementProcessItems.add(processItem);
        ReinstatementProcessInformation reinstatementProcessInformation = ReinstatementProcessInformation.create(reinstatementProcessItems);
        assertNotNull(reinstatementProcessInformation);
    }

    @Test
    public void givenQuotationProcessInformation_thenItShouldReturnTheQuotationProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> quotationProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        quotationProcessItems.add(processItem);
        QuotationProcessInformation quotationProcessInformation = QuotationProcessInformation.create(quotationProcessItems);
        assertNotNull(quotationProcessInformation);
    }

    @Test(expected = GeneralInformationException.class)
    public void givenQuotationProcessInformation_whenTheProcessTypeIsEarlyDeathCriteria_thenItShouldThrowAnException(){
        List<Map<ProductLineProcessType,Integer>> quotationProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.EARLY_DEATH_CRITERIA,12);
        quotationProcessItems.add(processItem);
        QuotationProcessInformation quotationProcessInformation = QuotationProcessInformation.create(quotationProcessItems);
        assertNotNull(quotationProcessInformation);
    }

    @Test
    public void givenEnrollmentProcessInformation_thenItShouldReturnTheEnrollmentProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        enrollmentProcessItems.add(processItem);
        EnrollmentProcessInformation enrollmentProcessInformation = EnrollmentProcessInformation.create(enrollmentProcessItems);
        assertNotNull(enrollmentProcessInformation);
    }

    @Test(expected = GeneralInformationException.class)
    public void givenEnrollmentProcessInformation_whenTheProcessTypeIsEarlyDeathCriteria_thenItShouldThrowAnException(){
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.EARLY_DEATH_CRITERIA,12);
        enrollmentProcessItems.add(processItem);
        EnrollmentProcessInformation enrollmentProcessInformation = EnrollmentProcessInformation.create(enrollmentProcessItems);
        assertNotNull(enrollmentProcessInformation);
    }


    @Test
    public void givenEndorsementProcessInformation_thenItShouldReturnTheEndorsementProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        endorsementProcessItems.add(processItem);
        EndorsementProcessInformation endorsementProcessInformation = EndorsementProcessInformation.create(endorsementProcessItems);
        assertNotNull(endorsementProcessInformation);
    }

    @Test(expected = GeneralInformationException.class)
    public void givenEndorsementProcessInformation_whenTheProcessTypeIsEarlyDeathCriteria_thenItShouldThrowAnException(){
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.EARLY_DEATH_CRITERIA,12);
        endorsementProcessItems.add(processItem);
        EndorsementProcessInformation endorsementProcessInformation = EndorsementProcessInformation.create(endorsementProcessItems);
        assertNotNull(endorsementProcessInformation);
    }

    @Test
    public void givenClaimProcessInformation_thenItShouldReturnTheClaimProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> claimProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        claimProcessItems.add(processItem);
        ClaimProcessInformation claimProcessInformation = ClaimProcessInformation.create(claimProcessItems);
        assertNotNull(claimProcessInformation);
    }

    @Test
    public void givenSurrenderProcessInformation_thenItShouldReturnTheSurrenderProcessInformation(){
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        surrenderProcessItems.add(processItem);
        SurrenderProcessInformation surrenderProcessInformation = SurrenderProcessInformation.create(surrenderProcessItems);
        assertNotNull(surrenderProcessInformation);
    }

    @Test(expected = GeneralInformationException.class)
    public void givenSurrenderProcessInformation_whenTheProcessTypeIsEarlyDeathCriteria_thenItShouldThrowAnException(){
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.EARLY_DEATH_CRITERIA,12);
        surrenderProcessItems.add(processItem);
        SurrenderProcessInformation surrenderProcessInformation = SurrenderProcessInformation.create(surrenderProcessItems);
        assertNotNull(surrenderProcessInformation);
    }

    @Test
    public void givenMaturityProcessInformation_thenItShouldReturnTheMaturityProcessInformation1(){
        List<Map<ProductLineProcessType,Integer>> maturityProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        maturityProcessItems.add(processItem);
        MaturityProcessInformation maturityProcessInformation = MaturityProcessInformation.create(maturityProcessItems);
        assertNotNull(maturityProcessInformation);
    }

    @Test(expected = GeneralInformationException.class)
    public void givenMaturityProcessInformation_whenTheProcessTypeIsEarlyDeathCriteria_thenItShouldThrowAnException(){
        List<Map<ProductLineProcessType,Integer>> maturityProcessItems  = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(ProductLineProcessType.EARLY_DEATH_CRITERIA,12);
        maturityProcessItems.add(processItem);
        MaturityProcessInformation maturityProcessInformation = MaturityProcessInformation.create(maturityProcessItems);
        assertNotNull(maturityProcessInformation);
    }


    @Test
    public void givenPolicyFeeProcessInformation_thenItShouldReturnThePolicyFeeProcessInformation(){
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessItems  = Lists.newArrayList();
        Map<PolicyFeeProcessType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(PolicyFeeProcessType.MONTHLY,12);
        policyFeeProcessItems.add(processItem);
        PolicyFeeProcessInformation policyFeeProcessInformation = PolicyFeeProcessInformation.create(policyFeeProcessItems);
        assertNotNull(policyFeeProcessInformation);
    }

    @Test
    public void givenMinimumLimitProcessInformation_thenItShouldReturnTheMinimumLimitProcessInformation(){
        List<Map<PolicyProcessMinimumLimitType,Integer>> policyMinimumLimitProcessItems  = Lists.newArrayList();
        Map<PolicyProcessMinimumLimitType,Integer> processItem = Maps.newLinkedHashMap();
        processItem.put(PolicyProcessMinimumLimitType.MINIMUM_PREMIUM,12);
        policyMinimumLimitProcessItems.add(processItem);
        PolicyProcessMinimumLimit policyProcessMinimumLimit = PolicyProcessMinimumLimit.create(policyMinimumLimitProcessItems);
        assertNotNull(policyProcessMinimumLimit);
    }

    @Test
    public void givenProductLineProcessType_thenItShouldReturnProductLineProcess(){
        ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        assertNotNull(productLineProcessItem);
        assertEquals(ProductLineProcessType.PURGE_TIME_PERIOD, invokeGetterMethod(productLineProcessItem, "productLineProcessItem"));
    }

    @Test
    public void givenProductLineMinimumLimitType_thenItShouldReturnProductLineMinimumLimitProcess(){
        PolicyProcessMinimumLimitItem policyProcessMinimumLimitItem = new PolicyProcessMinimumLimitItem(PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY,12);
        assertNotNull(policyProcessMinimumLimitItem);
        assertEquals(PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY, invokeGetterMethod(policyProcessMinimumLimitItem, "policyProcessMinimumLimitType"));
    }

    @Test
    public void givenProductLinePolicyFeeProcess_thenItShouldReturnProductLinePolicyFeeProcess(){
        PolicyFeeProcessItem policyFeeProcessItem = new PolicyFeeProcessItem(PolicyFeeProcessType.MONTHLY,12);
        assertNotNull(policyFeeProcessItem);
        assertEquals(PolicyFeeProcessType.MONTHLY, invokeGetterMethod(policyFeeProcessItem, "policyFeeProcessType"));
    }

    @Test
    public void givenPremiumFollowUpFrequency_whenPremiumFrequencyIsAnnual_thenItShouldReturnProductLineProcessItemOfAnnualPremiumFrequency(){
        Set<PremiumFollowUpFrequency> premiumFollowUpFrequencyList = Sets.newLinkedHashSet();
        Set<ProductLineProcessItem> premiumFollowUpFrequencyItems = Sets.newLinkedHashSet();
        ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(ProductLineProcessType.LAPSE,0);
        premiumFollowUpFrequencyItems.add(productLineProcessItem);
        PremiumFollowUpFrequency premiumFollowUpFrequency = new PremiumFollowUpFrequency(PremiumFrequency.ANNUALLY,premiumFollowUpFrequencyItems);
        premiumFollowUpFrequencyList.add(premiumFollowUpFrequency);

        ProductLineGeneralInformation productLineGeneralInformation = new ProductLineGeneralInformation();
        productLineGeneralInformation.setPremiumFollowUpFrequency(premiumFollowUpFrequencyList);
        Set<ProductLineProcessItem>  productLineProcessItems =  productLineGeneralInformation.getPremiumFollowUpFrequencyFor(PremiumFrequency.ANNUALLY);
        assertThat(premiumFollowUpFrequencyItems,is(productLineProcessItems));
    }

    @Test
    public void givenPremiumFollowUpFrequency_whenPremiumFrequencyIsSemiAnnual_thenItShouldReturnProductLineProcessItemOfSemiAnnualPremiumFrequency(){
        Set<PremiumFollowUpFrequency> premiumFollowUpFrequencyList = Sets.newLinkedHashSet();
        Set<ProductLineProcessItem> premiumFollowUpFrequencyItems = Sets.newLinkedHashSet();
        ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(ProductLineProcessType.FIRST_REMAINDER,0);
        premiumFollowUpFrequencyItems.add(productLineProcessItem);
        PremiumFollowUpFrequency premiumFollowUpFrequency = new PremiumFollowUpFrequency(PremiumFrequency.SEMI_ANNUALLY,premiumFollowUpFrequencyItems);
        premiumFollowUpFrequencyList.add(premiumFollowUpFrequency);

        ProductLineGeneralInformation productLineGeneralInformation = new ProductLineGeneralInformation();
        productLineGeneralInformation.setPremiumFollowUpFrequency(premiumFollowUpFrequencyList);
        Set<ProductLineProcessItem>  productLineProcessItems =  productLineGeneralInformation.getPremiumFollowUpFrequencyFor(PremiumFrequency.SEMI_ANNUALLY);
        assertThat(premiumFollowUpFrequencyItems,is(productLineProcessItems));
    }

    @Test
    public void givenPremiumFollowUpFrequency_whenPremiumFrequencyIsQuarterly_thenItShouldReturnProductLineProcessItemOfQuarterlyPremiumFrequency(){
        Set<PremiumFollowUpFrequency> premiumFollowUpFrequencyList = Sets.newLinkedHashSet();
        Set<ProductLineProcessItem> premiumFollowUpFrequencyItems = Sets.newLinkedHashSet();
        ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(ProductLineProcessType.SECOND_REMAINDER,0);
        premiumFollowUpFrequencyItems.add(productLineProcessItem);
        PremiumFollowUpFrequency premiumFollowUpFrequency = new PremiumFollowUpFrequency(PremiumFrequency.QUARTERLY,premiumFollowUpFrequencyItems);
        premiumFollowUpFrequencyList.add(premiumFollowUpFrequency);

        ProductLineGeneralInformation productLineGeneralInformation = new ProductLineGeneralInformation();
        productLineGeneralInformation.setPremiumFollowUpFrequency(premiumFollowUpFrequencyList);
        Set<ProductLineProcessItem>  productLineProcessItems =  productLineGeneralInformation.getPremiumFollowUpFrequencyFor(PremiumFrequency.QUARTERLY);
        assertThat(premiumFollowUpFrequencyItems,is(productLineProcessItems));
    }

    @Test
    public void givenPremiumFollowUpFrequency_whenPremiumFrequencyIsMonthly_thenItShouldReturnProductLineProcessItemOfMonthlyPremiumFrequency(){
        Set<PremiumFollowUpFrequency> premiumFollowUpFrequencyList = Sets.newLinkedHashSet();
        Set<ProductLineProcessItem> premiumFollowUpFrequencyItems = Sets.newLinkedHashSet();
        ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(ProductLineProcessType.LAPSE,0);
        premiumFollowUpFrequencyItems.add(productLineProcessItem);
        PremiumFollowUpFrequency premiumFollowUpFrequency = new PremiumFollowUpFrequency(PremiumFrequency.MONTHLY,premiumFollowUpFrequencyItems);
        premiumFollowUpFrequencyList.add(premiumFollowUpFrequency);

        ProductLineGeneralInformation productLineGeneralInformation = new ProductLineGeneralInformation();
        productLineGeneralInformation.setPremiumFollowUpFrequency(premiumFollowUpFrequencyList);
        Set<ProductLineProcessItem>  productLineProcessItems =  productLineGeneralInformation.getPremiumFollowUpFrequencyFor(PremiumFrequency.MONTHLY);
        assertThat(premiumFollowUpFrequencyItems,is(productLineProcessItems));
    }
}
