package com.pla.core.domain.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.ProcessType;
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

    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsProposalAndProcessTypeIsClosureTimePeriod_thenItShouldReturnTheClosureTimePeriodOfProposalProcess(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
        assertThat(closureTimePeriod,is(14));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsQuotationAndProcessTypePurgeTimePeriod_thenItShouldReturnThePurgeTimePeriodOfQuotationProcess(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(10));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsQuotationAndTheProcessTypeIsFirstReminder_thenItShouldReturnTheDaysForFirstReminder(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(12));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsQuotationAndProcessTypeIsSecondReminder_thenItShouldReturnTheDaysForSecondReminder(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(11));
    }

    @Test
    public void givenLineOfBusinessIdAndProcessType_whenProcessTypeIsEndorsementAndTheProcessTypeIsSecondReminder_thenItShouldReturnTheDaysForSecondReminder(){
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation = productLineGeneralInformation.withQuotationProcessInformation(listOfProcessItems);
        productLineGeneralInformation = productLineGeneralInformation.withEnrollmentProcessGeneralInformation(listOfProcessItems);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        int closureTimePeriod =  processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        assertThat(closureTimePeriod,is(11));
    }
}
