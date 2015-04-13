package com.pla.core.application.service.plan.premium;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.exception.PremiumTemplateParseException;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.premium.PremiumInfluencingFactor;
import com.pla.core.query.MasterFinder;
import com.pla.sharedkernel.identifier.CoverageId;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 4/9/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class PremiumTemplateParserUnitTest {

    @Mock
    MasterFinder masterFinder;

    PremiumTemplateParser premiumTemplateParser ;
    HSSFWorkbook premiumTemplateWorkbook;
    HSSFSheet premiumSheet;
    List<Row> allRows;
    List<Row> allRowsToBeCompared;
    @Mock
    Plan plan;
    @Before
    public void setUp() throws IOException {
        premiumTemplateParser =  new PremiumTemplateParser(masterFinder);
        InputStream inputStream =  ClassLoader.getSystemResourceAsStream("testdata/endtoend/plan/_PremiumTemplate.xls");
        POIFSFileSystem fs = new POIFSFileSystem(inputStream);
        premiumTemplateWorkbook = new HSSFWorkbook(fs);
        premiumSheet = premiumTemplateWorkbook.getSheetAt(0);
        Iterator<Row> rowsIterator = premiumSheet.iterator();
        allRows = Lists.newArrayList(rowsIterator);
        allRowsToBeCompared = Lists.newArrayList(allRows);
    }

    @Test
    public void givenSecondRowAndThirdRow_whenBothRowsAreNotIdentical_thenItShouldReturnTrue(){
        Row firstRow  = allRows.get(2);
        Row secondRow  = allRowsToBeCompared.get(3);
        Boolean isRowIdentical = premiumTemplateParser.isTwoRowIdentical(firstRow, secondRow, 3);
        assertTrue(isRowIdentical);
    }

    @Test
    public void givenFirstRowAndSecondRow_whenBothRowsAreNotIdentical_thenItShouldReturnFalse(){
        Row firstRow  = allRows.get(1);
        Row secondRow  = allRowsToBeCompared.get(2);
        Boolean isRowIdentical = premiumTemplateParser.isTwoRowIdentical(firstRow, secondRow, 3);
        assertFalse(isRowIdentical);
    }


    @Test
    public void givenFirstRowAndSecondRow_whenAllCellAreIdentical_thenItShouldReturnTrue(){
        Row firstRow  = allRows.get(2);
        Row secondRow  = allRowsToBeCompared.get(3);
        List<Cell> firstRowCellList = premiumTemplateParser.transformCellIteratorToList(firstRow.cellIterator());
        List<Cell> secondRowCellList = premiumTemplateParser.transformCellIteratorToList(secondRow.cellIterator());
        Boolean doesAllRowContainTheUniqueValue = premiumTemplateParser.areAllCellContainsUniqueValue(firstRowCellList,secondRowCellList,3,4);
        assertTrue(doesAllRowContainTheUniqueValue);
    }

    @Test
    public void givenFirstRowAndSecondRow_whenAllCellAreNotIdentical_thenItShouldReturnFalse(){
        Row firstRow  = allRows.get(1);
        Row secondRow  = allRowsToBeCompared.get(2);
        List<Cell> firstRowCellList = premiumTemplateParser.transformCellIteratorToList(firstRow.cellIterator());
        List<Cell> secondRowCellList = premiumTemplateParser.transformCellIteratorToList(secondRow.cellIterator());
        Boolean doesAllRowContainTheUniqueValue = premiumTemplateParser.areAllCellContainsUniqueValue(firstRowCellList,secondRowCellList,3,4);
        assertFalse(doesAllRowContainTheUniqueValue);
    }

    @Test(expected = PremiumTemplateParseException.class)
    public void givenAPremiumSheet_whenTheTemplateHasEmptyRows_thenItShouldThrowAnException(){
        int noOfNonEmptyRow =  premiumTemplateParser.getNoOfNonEmptyRow(premiumSheet);
    }

    @Test
    public void givenTheRow_whenTheRowIsEmpty_thenItShouldReturnTrue(){
        Row row  = allRows.get(5);
        boolean isRowEmpty = premiumTemplateParser.isRowEmpty(row);
        assertTrue(isRowEmpty);
    }

    @Test
    public void givenTheRow_whenTheRowIsNotEmpty_thenItShouldReturnFalse(){
        Row row  = allRows.get(2);
        boolean isRowEmpty = premiumTemplateParser.isRowEmpty(row);
        assertFalse(isRowEmpty);
    }

    @Test
    public void givenHeaderAndTheListOfInfluencingFactor_whenHeaderIsSameAsThePremiumInfluencingFactor_thenItShouldReturnTrue(){
        boolean isValidHeader = premiumTemplateParser.isValidHeader(allRows.get(7),Lists.newArrayList(PremiumInfluencingFactor.SUM_ASSURED,PremiumInfluencingFactor.AGE,PremiumInfluencingFactor.SMOKING_STATUS,PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM));
        assertTrue(isValidHeader);
    }

    @Test
    public void givenHeaderAndTheListOfInfluencingFactor_whenHeaderIsNotSameAsThePremiumInfluencingFactor_thenItShouldReturnFalse(){
        boolean isValidHeader = premiumTemplateParser.isValidHeader(allRows.get(0),Lists.newArrayList(PremiumInfluencingFactor.SUM_ASSURED,PremiumInfluencingFactor.AGE,PremiumInfluencingFactor.SMOKING_STATUS,PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM));
        assertFalse(isValidHeader);
    }

    @Test
    public void givenTheInfluencingFactorNameAndListHeaderRow_whenTheInfluencingFactorNameIsPresent_thenItShouldReturnTheCellIndex(){
        Row row =  allRows.get(7);
        List<Cell> headerRowCellList = premiumTemplateParser.transformCellIteratorToList(row.cellIterator());
        int cellNumberForAge = premiumTemplateParser.getCellNumberFor("Age", headerRowCellList);
        assertThat(cellNumberForAge,is(1));
    }

    @Test
    public void givenPremiumInfluencingFactorCoverageIdAndPlan_when_thenItShouldReturnTotalNumberOfPremiumCombination(){
        int totalNumberOfPremiumCombination = premiumTemplateParser.getTotalNoOfPremiumCombination(Lists.newArrayList(PremiumInfluencingFactor.SUM_ASSURED, PremiumInfluencingFactor.AGE, PremiumInfluencingFactor.SMOKING_STATUS, PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM), new CoverageId("1"), plan);
        assertThat(totalNumberOfPremiumCombination,is(2));
    }


    @Test
    public void givenHeaderRowAndListInfluencingFactor_thenItShouldReturnInfluencingFactorWithCelIndex(){
        Map<PremiumInfluencingFactor, Integer> expectedInfluencingFactorWithCellIndexMap = Maps.newLinkedHashMap();
        expectedInfluencingFactorWithCellIndexMap.put(PremiumInfluencingFactor.SUM_ASSURED,0);
        expectedInfluencingFactorWithCellIndexMap.put(PremiumInfluencingFactor.AGE,1);
        expectedInfluencingFactorWithCellIndexMap.put(PremiumInfluencingFactor.SMOKING_STATUS,2);

        Row row =  allRows.get(7);
        Map<PremiumInfluencingFactor, Integer> influencingFactorCellIndexMap = premiumTemplateParser.buildInfluencingFactorAndCellIndexMap(row, Lists.newArrayList(PremiumInfluencingFactor.SUM_ASSURED, PremiumInfluencingFactor.AGE, PremiumInfluencingFactor.SMOKING_STATUS));
        assertThat(expectedInfluencingFactorWithCellIndexMap,is(influencingFactorCellIndexMap));
    }

    @Test(expected = NoSuchElementException.class)
    public void givenHeaderRowAndListInfluencingFactor_whenTheHeaderRowIsInvalid_thenItShouldThrowAnException(){
        Row row =  allRows.get(0);
        Map<PremiumInfluencingFactor, Integer> influencingFactorCellIndexMap = premiumTemplateParser.buildInfluencingFactorAndCellIndexMap(row, Lists.newArrayList(PremiumInfluencingFactor.SUM_ASSURED, PremiumInfluencingFactor.AGE, PremiumInfluencingFactor.SMOKING_STATUS,PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM));
        assertNull(influencingFactorCellIndexMap);
    }

}
