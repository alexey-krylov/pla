package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Admin on 5/19/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnderWriterTemplateParserUnitTest {

    @Mock
    IPlanAdapter iPlanAdapter;

    HSSFWorkbook templateWorkbook;

    @Before
    public void setUp() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("testdata/endtoend/underwriter/downloadunderwritingtemplate.xls");
        POIFSFileSystem fs = new POIFSFileSystem(inputStream);
        templateWorkbook = new HSSFWorkbook(fs);
    }


    @Test
    public void givenTemplate_whenTheSelectedInfluencingFactorsAreNotInTemplate_thenThrowParseException() throws Exception {
        UnderWriterTemplateParser underWriterTemplateParser = new UnderWriterTemplateParser();
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE, UnderWriterInfluencingFactor.SUM_ASSURED);
        List<Map<String, Object>> underWriterRoutingLevelItemFromTemplate = underWriterTemplateParser.parseUnderWriterRoutingLevelTemplate(templateWorkbook, underWriterInfluencingFactors);
        List<Map<Object,Map<String,Object>>>  underWriterRoutingLevelList =  underWriterTemplateParser.groupUnderWriterLineItemByInfluencingFactor(underWriterRoutingLevelItemFromTemplate, underWriterInfluencingFactors);
        assertNotNull(underWriterRoutingLevelList);
    }

    @Test
    public void givenTemplate_whenTheTemplateIsValid_thenItShouldReturnTrue() throws Exception {
        UnderWriterTemplateParser underWriterTemplateParser = new UnderWriterTemplateParser();
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE, UnderWriterInfluencingFactor.SUM_ASSURED);
        boolean isValid = underWriterTemplateParser.validateUnderWritingRoutingLevelDataForAGivenPlanAndCoverage(templateWorkbook,null,null,underWriterInfluencingFactors,iPlanAdapter);
        assertFalse(isValid);
    }

    @Test
    public void givenUnderWriterInfluencingFactor_itShouldReturnTheRangeOfEachInfluencingFactor(){
        UnderWriterTemplateParser underWriterTemplateParser = new UnderWriterTemplateParser();
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.BMI,UnderWriterInfluencingFactor.WEIGHT,UnderWriterInfluencingFactor.HEIGHT,UnderWriterInfluencingFactor.CLAIM_AMOUNT);
        List<String> underWritingInfluencingFactorArray = underWriterTemplateParser.convertToStringArray(underWriterInfluencingFactors);
        assertNotNull(underWritingInfluencingFactorArray);
    }

    @Test
    public void givenAUnderWriterTemplate_whenAnyOneOfTheInfluencingFactorIsNotOverLapping_thenItShouldReturnTheValidTemplateAsTrue() throws IOException {
        HSSFSheet underWriterDocumentSheet  = templateWorkbook.getSheetAt(0);
        List<Row> rows = Lists.newArrayList(underWriterDocumentSheet.iterator());
        Row currentRow = rows.get(1);
        Row comparedBy = rows.get(2);
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.SUM_ASSURED);
        UnderWriterTemplateParser underWriterTemplateParser = new UnderWriterTemplateParser();
        boolean isOverLapping = underWriterTemplateParser.isRowOverlapping(currentRow,comparedBy,underWriterTemplateParser.convertToStringArray(underWriterInfluencingFactors));
        assertFalse(isOverLapping);
    }

    @Test
    public void givenAUnderWriterTemplate_whenTheAllInfluencingFactorInARowAreOverLapping_thenItShouldReturnTheValidTemplateAsFalse() throws IOException {
        HSSFSheet underWriterDocumentSheet  = templateWorkbook.getSheetAt(0);
        List<Row> rows = Lists.newArrayList(underWriterDocumentSheet.iterator());
        Row currentRow = rows.get(1);
        Row comparedBy = rows.get(3);
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.SUM_ASSURED);
        UnderWriterTemplateParser underWriterTemplateParser = new UnderWriterTemplateParser();
        boolean isOverLapping = underWriterTemplateParser.isRowOverlapping(currentRow, comparedBy, underWriterTemplateParser.convertToStringArray(underWriterInfluencingFactors));
        assertTrue(isOverLapping);
    }

}
