package com.pla.core.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.Admin;
import com.pla.core.dto.ProductLineProcessItemDto;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
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
    private UserDetails userDetails;
    private Admin admin;

    private GeneralInformationService generalInformationService;
    List<ProductLineProcessItemDto> processItemDtos;

    @Before
    public void setUp() {
        generalInformationService = new GeneralInformationService(adminRoleAdapter,springMongoTemplate);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin();

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
}
