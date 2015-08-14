package com.pla.underwriter.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
import com.pla.underwriter.service.UnderWriterTemplateParser;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.nthdimenzion.common.AppConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 5/11/2015.
 */
public class UnderWriterRoutingLevelUnitTest {

    List<Map<Object,Map<String,Object>>> underWriterDocumentItem;

    @Before
    public void setUp(){

        underWriterDocumentItem = Lists.newArrayList();
        Map<Object,Map<String,Object>> underWriterLineItems = Maps.newLinkedHashMap();
        Map<String,Object> underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("Age From","20");
        underWriterItem.put("Age To","30");
        underWriterLineItems.put(UnderWriterInfluencingFactor.AGE,underWriterItem);

        underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("Sum Assured From","2000");
        underWriterItem.put("Sum Assured To","3000");
        underWriterLineItems.put(UnderWriterInfluencingFactor.SUM_ASSURED,underWriterItem);

        underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,"UnderWriting level 1");
        underWriterLineItems.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,underWriterItem);

        underWriterDocumentItem.add(underWriterLineItems);

        /*
        * Weight in Kgs
        * */
        Map<Object,Map<String,Object>>   underWriterLineItemsOne = Maps.newLinkedHashMap();
        Map<String,Object> underWriterItemOne = Maps.newLinkedHashMap();
        underWriterItemOne.put("Weight From","40");
        underWriterItemOne.put("Weight To","50");
        underWriterLineItemsOne.put(UnderWriterInfluencingFactor.WEIGHT, underWriterItemOne);

        /*
        * height in centimeters
        * */
        underWriterItemOne = Maps.newLinkedHashMap();
        underWriterItemOne.put("Height From","176");
        underWriterItemOne.put("Height To","180");
        underWriterLineItemsOne.put(UnderWriterInfluencingFactor.HEIGHT,underWriterItemOne);

        underWriterItemOne = Maps.newLinkedHashMap();
        underWriterItemOne.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,"UnderWriting level 2");
        underWriterLineItemsOne.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,underWriterItemOne);
        underWriterDocumentItem.add(underWriterLineItemsOne);

    }

    @Test
    public void givenUnderWriterRoutingLevelSetUp_thenItShouldCreateTheUnderWriterRoutingLevel(){
        Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems = UnderWriterRoutingLevel.withUnderWritingLevelItem(underWriterDocumentItem);
        assertNotNull(underWritingRoutingLevelItems);

    }

    @Test
    public void givenUnderWriterRoutingLevelSetUp_thenItShouldCreateRoutingLevelONE(){
        Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems = UnderWriterRoutingLevel.withUnderWritingLevelItem(underWriterDocumentItem);
        assertNotNull(underWritingRoutingLevelItems);

    }

    @Test
    public void givenUnderWriterInfluencingFactor_itShouldReturnTheRangeOfEachInfluencingFactor(){
        UnderWriterTemplateParser underWriterTemplateParser = new UnderWriterTemplateParser();
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.BMI,UnderWriterInfluencingFactor.WEIGHT,UnderWriterInfluencingFactor.HEIGHT,UnderWriterInfluencingFactor.CLAIM_AMOUNT);
        List<String> underWritingInfluencingFactorArray = underWriterTemplateParser.convertToStringArray(underWriterInfluencingFactors);
        assertNotNull(underWritingInfluencingFactorArray);
    }

    @Test
    public void givenAPlanCodeAndUnderWriterInfluencingFactor_whenAllRequiredValuesAreNotNull_thenItShouldCreateTheUnderWriterRoutingLevelWithPlan(){
        UnderWriterRoutingLevelId underWriterRoutingLevelId = new UnderWriterRoutingLevelId("UR001");
        PlanId planId =  new PlanId("P001");
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE);
        UnderWriterRoutingLevel underWriterRoutingLevel = UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithPlan(underWriterRoutingLevelId, planId, UnderWriterProcessType.CLAIM, underWriterDocumentItem, underWriterInfluencingFactors, new LocalDate("2016-12-31"));
        assertNotNull(underWriterRoutingLevel);
        assertThat( new PlanId("P001"), is(underWriterRoutingLevel.getPlanId()));
        assertThat(underWriterInfluencingFactors.size(),is(underWriterRoutingLevel.getUnderWriterInfluencingFactors().size()));
        assertNull(underWriterRoutingLevel.getCoverageId());
    }

    @Test
    public void givenAPlanCodeCoverageIdAndUnderWriterInfluencingFactor_whenAllRequiredValuesAreNotNull_thenItShouldCreateTheUnderWriterRoutingLevelWithPlanAndCoverage(){
        UnderWriterRoutingLevelId underWriterRoutingLevelId = new UnderWriterRoutingLevelId("UR001");
        PlanId planCode =  new PlanId("P002");
        CoverageId coverageId = new CoverageId("C002");
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE);
        UnderWriterRoutingLevel underWriterRoutingLevel = UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithOptionalCoverage(underWriterRoutingLevelId, planCode, coverageId, UnderWriterProcessType.CLAIM, underWriterDocumentItem, underWriterInfluencingFactors, new LocalDate("2016-12-31"));
        assertNotNull(underWriterRoutingLevel);
        assertThat( new PlanId("P002"), is(underWriterRoutingLevel.getPlanId()));
        assertThat(underWriterInfluencingFactors.size(),is(underWriterRoutingLevel.getUnderWriterInfluencingFactors().size()));
        assertNotNull(underWriterRoutingLevel.getCoverageId());
    }

    @Test
    public void givenAExpireDate_thenItShouldAssignTheExpireDateToTheUnderWriterRoutingLevel(){
        UnderWriterRoutingLevel underWriterRoutingLevel =getUnderWriterRoutingLevel();
        underWriterRoutingLevel.expireUnderWriterRoutingLevel(new LocalDate("2017-01-01"));
        assertThat(underWriterRoutingLevel.getValidTill(),is(new LocalDate("2017-01-01")));
    }

    @Test
    public void givenUnderWriterInfluencingFactor_whenUnderWriterRoutingLevelHasAllTheInfluencingFactors_thenItShouldReturnTrue(){
        List<String> expectedInfluencingFactorList = Lists.newArrayList(UnderWriterInfluencingFactor.AGE.name(),UnderWriterInfluencingFactor.SUM_ASSURED.name());
        UnderWriterRoutingLevel underWriterRoutingLevel = getUnderWriterRoutingLevel();
        boolean hasAllInfluencingFactor = underWriterRoutingLevel.hasAllInfluencingFactor(expectedInfluencingFactorList);
        assertTrue(hasAllInfluencingFactor);
    }

    @Test
    public void givenUnderWriterInfluencingFactor_whenUnderWriterRoutingLevelDoesNotHaveAllTheInfluencingFactors_thenItShouldReturnFalse(){
        List<String> expectedInfluencingFactorList = Lists.newArrayList(UnderWriterInfluencingFactor.AGE.name(),UnderWriterInfluencingFactor.SUM_ASSURED.name(),UnderWriterInfluencingFactor.BMI.name());
        UnderWriterRoutingLevel underWriterRoutingLevel = getUnderWriterRoutingLevel();
        boolean hasAllInfluencingFactor = underWriterRoutingLevel.hasAllInfluencingFactor(expectedInfluencingFactorList);
        assertFalse(hasAllInfluencingFactor);
    }

    private UnderWriterRoutingLevel getUnderWriterRoutingLevel(){
        UnderWriterRoutingLevelId underWriterRoutingLevelId = new UnderWriterRoutingLevelId("UR001");
        PlanId planId =  new PlanId("P002");
        CoverageId coverageId = new CoverageId("C002");
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.SUM_ASSURED);
        return UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithOptionalCoverage(underWriterRoutingLevelId, planId, coverageId, UnderWriterProcessType.CLAIM, underWriterDocumentItem, underWriterInfluencingFactors, new LocalDate("2016-12-30"));
    }

}
