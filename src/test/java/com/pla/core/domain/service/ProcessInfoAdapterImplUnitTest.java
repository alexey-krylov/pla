package com.pla.core.domain.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 6/1/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessInfoAdapterImplUnitTest {


    @Mock
    private GeneralInformationService generalInformationService;

    ProcessInfoAdapterImpl processInfoAdapter;

    List<Map<ProductLineProcessType,Integer>> listOfProcessItems = Lists.newArrayList();
    Map<ProductLineProcessType,Integer> productLineProcessItemMap;

    List<Map<ProductLineProcessType,Integer>> listOfAnnualPremiumFrequencyProcessItems;
    List<Map<ProductLineProcessType,Integer>> listOfSemiAnnualPremiumFrequencyProcessItems;
    List<Map<ProductLineProcessType,Integer>> listOfQuarterlyPremiumFrequencyProcessItems;
    List<Map<ProductLineProcessType,Integer>> listOfMonthlyPremiumFrequencyProcessItems;

    @Before
    public void setUp(){
        processInfoAdapter = new ProcessInfoAdapterImpl(generalInformationService);
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

        /*
        * Annual Premium Follow up frequency
        * */
        listOfAnnualPremiumFrequencyProcessItems = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> productLineProcessItemMap  = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.SECOND_REMAINDER, 51);
        listOfAnnualPremiumFrequencyProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.FIRST_REMAINDER,52);
        listOfAnnualPremiumFrequencyProcessItems.add(productLineProcessItemMap);

        productLineProcessItemMap = Maps.newLinkedHashMap();
        productLineProcessItemMap.put(ProductLineProcessType.LAPSE, 53);
        listOfAnnualPremiumFrequencyProcessItems.add(productLineProcessItemMap);

        /*
        * Semi Annual Premium Follow up frequency
        * */
        listOfSemiAnnualPremiumFrequencyProcessItems = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> semiAnnualProductLineProcessItemMap  = Maps.newLinkedHashMap();
        semiAnnualProductLineProcessItemMap.put(ProductLineProcessType.SECOND_REMAINDER, 21);
        listOfSemiAnnualPremiumFrequencyProcessItems.add(semiAnnualProductLineProcessItemMap);

        semiAnnualProductLineProcessItemMap = Maps.newLinkedHashMap();
        semiAnnualProductLineProcessItemMap.put(ProductLineProcessType.FIRST_REMAINDER,22);
        listOfSemiAnnualPremiumFrequencyProcessItems.add(semiAnnualProductLineProcessItemMap);

        semiAnnualProductLineProcessItemMap = Maps.newLinkedHashMap();
        semiAnnualProductLineProcessItemMap.put(ProductLineProcessType.LAPSE, 24);
        listOfSemiAnnualPremiumFrequencyProcessItems.add(semiAnnualProductLineProcessItemMap);

        /*
        * Quarterly Premium Follow up frequency
        * */
        listOfQuarterlyPremiumFrequencyProcessItems = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> quarterlyProductLineProcessItemMap  = Maps.newLinkedHashMap();
        quarterlyProductLineProcessItemMap.put(ProductLineProcessType.SECOND_REMAINDER, 31);
        listOfQuarterlyPremiumFrequencyProcessItems.add(quarterlyProductLineProcessItemMap);

        quarterlyProductLineProcessItemMap = Maps.newLinkedHashMap();
        quarterlyProductLineProcessItemMap.put(ProductLineProcessType.FIRST_REMAINDER,31);
        listOfQuarterlyPremiumFrequencyProcessItems.add(quarterlyProductLineProcessItemMap);

        quarterlyProductLineProcessItemMap = Maps.newLinkedHashMap();
        quarterlyProductLineProcessItemMap.put(ProductLineProcessType.LAPSE, 34);
        listOfQuarterlyPremiumFrequencyProcessItems.add(quarterlyProductLineProcessItemMap);

        /*
        *
        * Monthly Premium Follow up frequency
        * */
        listOfMonthlyPremiumFrequencyProcessItems = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> monthlyProductLineProcessItemMap  = Maps.newLinkedHashMap();
        monthlyProductLineProcessItemMap.put(ProductLineProcessType.SECOND_REMAINDER, 11);
        listOfMonthlyPremiumFrequencyProcessItems.add(monthlyProductLineProcessItemMap);

        monthlyProductLineProcessItemMap = Maps.newLinkedHashMap();
        monthlyProductLineProcessItemMap.put(ProductLineProcessType.FIRST_REMAINDER,12);
        listOfMonthlyPremiumFrequencyProcessItems.add(monthlyProductLineProcessItemMap);

        monthlyProductLineProcessItemMap = Maps.newLinkedHashMap();
        monthlyProductLineProcessItemMap.put(ProductLineProcessType.LAPSE, 14);
        listOfMonthlyPremiumFrequencyProcessItems.add(monthlyProductLineProcessItemMap);

    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsProposalAndProcessTypeIsClosureTimePeriod_thenItShouldReturnTheClosureTimePeriodOfProposalProcess() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
        assertThat(closureTimePeriod,is(14));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsQuotationAndProcessTypePurgeTimePeriod_thenItShouldReturnThePurgeTimePeriodOfQuotationProcess() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(10));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsQuotationAndTheProcessTypeIsFirstReminder_thenItShouldReturnTheDaysForFirstReminder() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(12));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsQuotationAndProcessTypeIsSecondReminder_thenItShouldReturnTheDaysForSecondReminder() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(11));
    }

    @Test(expected = ProcessInfoException.class)
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsEndorsementAndTheProcessTypeIsSecondReminder_thenItShouldReturnTheDaysForSecondReminder() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.ENDORSEMENT);
        assertThat(closureTimePeriod,is(11));
    }

    @Test
    public void givenLineOfBusinessIdAndPremiumFollowUpFrequency_whenPremiumFrequencyIsAnnualAndProcessTypeIsLapseTimePeriod_thenItShouldReturnTheLapseTimePeriodOfAnnualPremiumFrequency() throws ProcessInfoException {
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFollowUpFrequencyItems = Maps.newLinkedHashMap();
        premiumFollowUpFrequencyItems.put(PremiumFrequency.ANNUALLY,listOfAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.SEMI_ANNUALLY,listOfSemiAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.QUARTERLY,listOfQuarterlyPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.MONTHLY,listOfMonthlyPremiumFrequencyProcessItems);
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withPremiumFollowUpMonthly(premiumFollowUpFrequencyItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int lapseTimePeriod =  processInfoAdapter.getPolicyLapseTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, PremiumFrequency.ANNUALLY);
        assertThat(lapseTimePeriod,is(53));
    }


    @Test
    public void givenLineOfBusinessIdAndPremiumFollowUpFrequency_whenPremiumFrequencyIsSemiAnnualAndProcessTypeIsFirstReminder_thenItShouldReturnTheFirstReminderOfSemiAnnualPremiumFrequency() throws ProcessInfoException {
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFollowUpFrequencyItems = Maps.newLinkedHashMap();
        premiumFollowUpFrequencyItems.put(PremiumFrequency.ANNUALLY,listOfAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.SEMI_ANNUALLY,listOfSemiAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.QUARTERLY,listOfQuarterlyPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.MONTHLY,listOfMonthlyPremiumFrequencyProcessItems);
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withPremiumFollowUpMonthly(premiumFollowUpFrequencyItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int daysForFirstReminder =  processInfoAdapter.getDaysForFirstPremiumReminder(LineOfBusinessEnum.GROUP_HEALTH, PremiumFrequency.SEMI_ANNUALLY);
        assertThat(daysForFirstReminder,is(22));
    }

    @Test
    public void givenLineOfBusinessIdAndPremiumFollowUpFrequency_whenPremiumFrequencyIsQuarterlyAndProcessTypeIsFirstReminder_thenItShouldReturnTheSecondReminderOfQuarterlyPremiumFrequency() throws ProcessInfoException {
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFollowUpFrequencyItems = Maps.newLinkedHashMap();
        premiumFollowUpFrequencyItems.put(PremiumFrequency.ANNUALLY,listOfAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.SEMI_ANNUALLY,listOfSemiAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.QUARTERLY,listOfQuarterlyPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.MONTHLY,listOfMonthlyPremiumFrequencyProcessItems);
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withPremiumFollowUpMonthly(premiumFollowUpFrequencyItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int daysForSecondReminder =  processInfoAdapter.getDaysForSecondPremiumReminder(LineOfBusinessEnum.GROUP_HEALTH, PremiumFrequency.QUARTERLY);
        assertThat(daysForSecondReminder,is(31));
    }

    @Test
    public void givenLineOfBusinessIdAndPremiumFollowUpFrequency_whenPremiumFrequencyIsMonthlyAndProcessTypeIsLapseTimePeriod_thenItShouldReturnTheLapseTimePeriodOfMonthlyPremiumFrequency() throws ProcessInfoException {
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFollowUpFrequencyItems = Maps.newLinkedHashMap();
        premiumFollowUpFrequencyItems.put(PremiumFrequency.ANNUALLY,listOfAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.SEMI_ANNUALLY,listOfSemiAnnualPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.QUARTERLY,listOfQuarterlyPremiumFrequencyProcessItems);
        premiumFollowUpFrequencyItems.put(PremiumFrequency.MONTHLY,listOfMonthlyPremiumFrequencyProcessItems);
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withPremiumFollowUpMonthly(premiumFollowUpFrequencyItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int lapseTimePeriod =  processInfoAdapter.getPolicyLapseTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, PremiumFrequency.MONTHLY);
        int daysForFirstReminder =  processInfoAdapter.getDaysForFirstPremiumReminder(LineOfBusinessEnum.GROUP_HEALTH, PremiumFrequency.MONTHLY);
        int daysForSecondReminder =  processInfoAdapter.getDaysForSecondPremiumReminder(LineOfBusinessEnum.GROUP_HEALTH, PremiumFrequency.MONTHLY);
        assertThat(lapseTimePeriod,is(14));
        assertThat(daysForSecondReminder,is(11));
        assertThat(daysForFirstReminder,is(12));
    }
}
