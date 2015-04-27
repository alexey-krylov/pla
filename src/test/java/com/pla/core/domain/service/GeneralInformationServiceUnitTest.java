package com.pla.core.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.dto.*;
import com.pla.sharedkernel.domain.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/23/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneralInformationServiceUnitTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MongoTemplate springMongoTemplate;

    private GeneralInformationService generalInformationService;
    List<ProductLineProcessItemDto> processItemDtos;

    @Before
    public void setUp() {
        generalInformationService = new GeneralInformationService(adminRoleAdapter,springMongoTemplate);

        processItemDtos = Lists.newArrayList();
        ProductLineProcessItemDto productLineProcessItemDto = new ProductLineProcessItemDto();
        productLineProcessItemDto.setProductLineProcessItem(ProductLineProcessType.PURGE_TIME_PERIOD);
        productLineProcessItemDto.setValue(12);
        processItemDtos.add(productLineProcessItemDto);

        productLineProcessItemDto = new ProductLineProcessItemDto();
        productLineProcessItemDto.setProductLineProcessItem(ProductLineProcessType.FIRST_REMAINDER);
        productLineProcessItemDto.setValue(14);
        processItemDtos.add(productLineProcessItemDto);

        productLineProcessItemDto = new ProductLineProcessItemDto();
        productLineProcessItemDto.setProductLineProcessItem(ProductLineProcessType.NO_OF_REMAINDER);
        productLineProcessItemDto.setValue(10);
        processItemDtos.add(productLineProcessItemDto);

        productLineProcessItemDto = new ProductLineProcessItemDto();
        productLineProcessItemDto.setProductLineProcessItem(ProductLineProcessType.CLOSURE);
        productLineProcessItemDto.setValue(18);
        processItemDtos.add(productLineProcessItemDto);

    }

    @Test
    public void givenProductLineProcessInformation_thenItShouldReturnTransformAndProvideTheExpectedProductLineInformation(){
        List<Map<ProductLineProcessType,Integer>> expectedProductLineProcessItem = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> processMap = Maps.newLinkedHashMap();
        processMap.put(ProductLineProcessType.PURGE_TIME_PERIOD,12);
        expectedProductLineProcessItem.add(processMap);

        processMap = Maps.newLinkedHashMap();
        processMap.put(ProductLineProcessType.FIRST_REMAINDER,14);
        expectedProductLineProcessItem.add(processMap);

        processMap = Maps.newLinkedHashMap();
        processMap.put(ProductLineProcessType.NO_OF_REMAINDER,10);
        expectedProductLineProcessItem.add(processMap);

        processMap = Maps.newLinkedHashMap();
        processMap.put(ProductLineProcessType.CLOSURE,18);
        expectedProductLineProcessItem.add(processMap);

        List<Map<ProductLineProcessType,Integer>> processItemMap = generalInformationService.transformProductLine(processItemDtos);
        assertThat(expectedProductLineProcessItem, is(processItemMap));
    }

    @Test
    public void givenPolicyProcessItem_thenItShouldReturnTheTransformedPolicyFeeProcessItems(){
        List<PolicyFeeProcessItemDto> policyFeeProcessItemDtos = Lists.newArrayList();
        PolicyFeeProcessItemDto policyFeeProcessItemDto  = new PolicyFeeProcessItemDto();
        policyFeeProcessItemDto.setPolicyFeeProcessType(PolicyFeeProcessType.MONTHLY);
        policyFeeProcessItemDto.setPolicyFee(12);
        policyFeeProcessItemDtos.add(policyFeeProcessItemDto);
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessType =  generalInformationService.transformProductLineFeeProcess(policyFeeProcessItemDtos);
        assertNotNull(policyFeeProcessType);
    }

    @Test
    public void givenProductLineMinimumProcessInformation_thenItShouldReturnTheTransformedProductLineMinimumProcessInformation(){
        List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItemDtos = Lists.newArrayList();
        PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY);
        policyProcessMinimumLimitItemDto.setValue(10);
        policyProcessMinimumLimitItemDtos.add(policyProcessMinimumLimitItemDto);
        List<Map<PolicyProcessMinimumLimitType,Integer>>  productLineMinimumLimitProcessItem = generalInformationService.transformProductLineMinimumLimitProcess(policyProcessMinimumLimitItemDtos);
        assertNotNull(productLineMinimumLimitProcessItem);
    }

    @Test
    public void givenModalFactorItems_thenItShouldReturnTheTransformedModalFactorInformation(){
        List<ModalFactorInformationDto> modalFactorInformationDtos = Lists.newArrayList();
        ModalFactorInformationDto modalFactorInformationDto = new ModalFactorInformationDto();
        modalFactorInformationDto.setModalFactorItem(ModalFactorItem.MONTHLY);
        modalFactorInformationDto.setValue(new BigDecimal(1234.4567));
        modalFactorInformationDtos.add(modalFactorInformationDto);
        List<Map<ModalFactorItem, BigDecimal>>  transformedModalFactorItem =  generalInformationService.transformModalFactorItem(modalFactorInformationDtos);
        assertNotNull(transformedModalFactorItem);
    }

    @Test
    public void givenDiscountFactorInformation_thenItShouldReturnTheTransformedDiscountFactorInformation(){
        List<DiscountFactorInformationDto> discountFactorInformationDtos = Lists.newArrayList();
        DiscountFactorInformationDto discountFactorInformationDto = new DiscountFactorInformationDto();
        discountFactorInformationDto.setDiscountFactorItem(DiscountFactorItem.ANNUAL);
        discountFactorInformationDto.setValue(new BigDecimal(1098.1238));
        discountFactorInformationDtos.add(discountFactorInformationDto);
        List<Map<DiscountFactorItem, BigDecimal>>  transformedDiscountFactor =  generalInformationService.transformDiscountFactorItem(discountFactorInformationDtos);
        assertNotNull(transformedDiscountFactor);
    }

}
