package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.dto.UnderWriterDto;
import com.pla.underwriter.dto.UnderWriterLineItemDto;
import com.pla.underwriter.exception.UnderWriterTemplateParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 5/21/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnderWriterServiceUnitTest {
    @Mock
    private UnderWriterTemplateParser underWriterTemplateParser;

    @Mock
    private IPlanAdapter iPlanAdapter;
    UnderWritingService underWritingService;
    List<UnderWriterDto> underWriterDocumentItems;

    @Before
    public void setUp(){
        underWriterDocumentItems = Lists.newArrayList();
        UnderWriterDto underWriterDto = new UnderWriterDto();

        List<UnderWriterLineItemDto> underWriterLineItem = Lists.newArrayList();
        UnderWriterLineItemDto underWriterLineItemDto = new UnderWriterLineItemDto();
        underWriterLineItemDto.setUnderWriterInfluencingFactor(UnderWriterInfluencingFactor.AGE);
        underWriterLineItemDto.setInfluencingItemFrom("10");
        underWriterLineItemDto.setInfluencingItemTo("30");
        underWriterLineItem.add(underWriterLineItemDto);
        underWriterDto.setUnderWriterLineItem(underWriterLineItem);
        Set<String> documents = Sets.newHashSet("Document One", "Document Two", "Document Three", "Document Four");
        underWriterDto.setDocuments(documents);
        underWriterDocumentItems.add(underWriterDto);

        underWritingService = new UnderWritingService(underWriterTemplateParser,iPlanAdapter);
    }

    /*
    * Given
    *   the under writer document setup
    * then
    *    it Should transform the setup to the expected form and return
    * */
    @Test
    public void givenUnderWriterDocumentSetUp_thenItShouldTransformTheUnderWriterDocumentSetUpToRequiredFormat(){
        List<Map<Object,Map<String,Object>>>  underWriterDocumentMap =  underWritingService.transformUnderWriterDocument(underWriterDocumentItems);
        assertNotNull(underWriterDocumentMap);
    }

    /*
    * Given
    *     the under writer document set up
    * When
    *     the Under writer influencing factor from/to values are overlapping with other respective influencing factors from/to value
    * then
    *    it should return isValidRow as false
    * */
    @Test
    public void givenUnderWriterDocumentSetUp_whenTheInfluencingFactorsAreOverLapping_thenItShouldReturnIsValidRowAsFalse(){
        UnderWriterDto underWriterDto = new UnderWriterDto();
        List<UnderWriterLineItemDto> underWriterLineItem = Lists.newArrayList();
        UnderWriterLineItemDto underWriterLineItemDto = new UnderWriterLineItemDto();
        underWriterLineItemDto.setUnderWriterInfluencingFactor(UnderWriterInfluencingFactor.AGE);
        underWriterLineItemDto.setInfluencingItemFrom("10");
        underWriterLineItemDto.setInfluencingItemTo("50");
        underWriterLineItem.add(underWriterLineItemDto);
        underWriterDto.setUnderWriterLineItem(underWriterLineItem);
        Set<String> documents = Sets.newHashSet("Document One", "Document Two", "Document Three", "Document Four");
        underWriterDto.setDocuments(documents);
        underWriterDocumentItems.add(underWriterDto);
        boolean isValid = underWritingService.doesAnyRowOverLappingEachOther(underWriterDto, underWriterDocumentItems, true, Lists.newArrayList());
        assertFalse(isValid);
    }

    /*
    * Given
    *     the under writer document set up
    * When
    *     the Under writer influencing factor from/to values are  not overlapping with other respective influencing factors from/to value
    * then
    *    it should return isValidRow as true
    * */
    @Test
    public void givenUnderWriterDocumentSetUp_whenTheInfluencingFactorsAreNotOverLapping_thenItShouldReturnIsValidRowAsTrue(){
        UnderWriterDto underWriterDto = new UnderWriterDto();
        List<UnderWriterLineItemDto> underWriterLineItem = Lists.newArrayList();
        UnderWriterLineItemDto underWriterLineItemDto = new UnderWriterLineItemDto();
        underWriterLineItemDto.setUnderWriterInfluencingFactor(UnderWriterInfluencingFactor.AGE);
        underWriterLineItemDto.setInfluencingItemFrom("40");
        underWriterLineItemDto.setInfluencingItemTo("50");
        underWriterLineItem.add(underWriterLineItemDto);
        underWriterDto.setUnderWriterLineItem(underWriterLineItem);
        Set<String> documents = Sets.newHashSet("Document One", "Document Two", "Document Three", "Document Four");
        underWriterDto.setDocuments(documents);
        underWriterDocumentItems.add(underWriterDto);
        boolean isValid = underWritingService.doesAnyRowOverLappingEachOther(underWriterDto, underWriterDocumentItems, true, Lists.newArrayList());
        assertTrue(isValid);
    }

    /*
    * Given the Influencing factor from/to value in string type
    * when the given value is a number
    * then it should convert the string to double and return the value
    * */
    @Test
    public void givenInfluencingFactorValue_whenTheValueIsValid_thenItShouldReturnTheInfluencingFactor(){
        Double expectedValue = 1234.0;
        Double influencingFactorValue =  underWritingService.getInfluencingFactorValue("1234");
        assertThat(expectedValue, is(influencingFactorValue));
    }

    /*
    * Given the Influencing factor from/to value in string type
    * when the given value is not a number
    * then it should return 0l
    * */
    @Test
    public void givenInfluencingFactorValue_whenTheValueIsNotValid_thenItShouldReturnTheInfluencingFactorAsZero(){
        Double expectedValue = 0.0;
        Double influencingFactorValue =  underWritingService.getInfluencingFactorValue("12ABC");
        assertThat(expectedValue, is(influencingFactorValue));
    }

    /*
    *
    * When the given plan code is valid,
    * Then it should allow to create the Under Writer document
    * */
    @Test
    public void givenAPlanCode_whenThePlanCodeIsValid_thenItShouldAllowToCreateTheUnderWriter(){
        when(iPlanAdapter.isValidPlanCode("P001")).thenReturn(true);
        underWritingService.checkValidPlanAndCoverageCode("P001");
    }

    /*
    * When The plan code is not existed
    * Then it should throw the exception with error message as Not a valid Plan.
    *
    * */
    @Test(expected = UnderWriterTemplateParseException.class)
    public void givenAPlanCode_whenThePlanCodeIsNotValid_thenItShouldThrowUnderWriterException(){
        when(iPlanAdapter.isValidPlanCode("P001")).thenReturn(false);
        underWritingService.checkValidPlanAndCoverageCode("P001");
    }
}
