package com.pla.underwriter.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 5/11/2015.
 */
public class UnderWriterDocumentUnitTest {

    List<Map<Object,Map<String,Object>>> underWriterDocument;

    @Before
    public void setUp(){
        underWriterDocument = Lists.newArrayList();
        Map<Object,Map<String,Object>> underWriterLineItems = Maps.newLinkedHashMap();
        Map<String,Object> underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("Age From","20");
        underWriterItem.put("Age To","30");
        underWriterLineItems.put(UnderWriterInfluencingFactor.AGE,underWriterItem);

        underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("Sum Assured From","2000");
        underWriterItem.put("Sum Assured To","3000");
        underWriterLineItems.put(UnderWriterInfluencingFactor.SUM_ASSURED,underWriterItem);

        Set<String> documents = Sets.newHashSet("Document One","Document Two","Document Three","Document Four");
        underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("documents",documents);
        underWriterLineItems.put("documents",underWriterItem);
        underWriterDocument.add(underWriterLineItems);
    }

    @Test
    public void givenUnderWriterDocumentSetUp_thenItShouldCreateTheUnderWriterDocument(){
        Set<String> documents = Sets.newHashSet("Document One","Document Two","Document Three","Document Four");
        Set<UnderWriterDocumentItem> underWriterDocumentItems =  UnderWriterDocument.withUnderWritingLevelItem(underWriterDocument);
        assertNotNull(underWriterDocumentItems);
        UnderWriterDocumentItem underWriterDocumentItem = underWriterDocumentItems.iterator().next();
        assertThat(underWriterDocumentItem.getDocumentIds(), is(documents));
    }

    @Test
    public void givenAPlanCodeAndUnderWriterInfluencingFactor_whenAllRequiredValuesAreNotNull_thenItShouldCreateTheUnderWriterDocumentWithPlan(){
        UnderWriterDocumentId underWriterRoutingLevelId = new UnderWriterDocumentId("UR001");
        String planCode = "P001";
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE);
        UnderWriterDocument  underWriterDocumentWithPlan  = UnderWriterDocument.createUnderWriterDocumentWithPlan(underWriterRoutingLevelId, planCode, UnderWriterProcessType.CLAIM, underWriterDocument, underWriterInfluencingFactors, new DateTime("2016-12-31"));
        assertNotNull(underWriterDocumentWithPlan);
        assertThat("P001", is(underWriterDocumentWithPlan.getPlanCode()));
        assertThat(underWriterInfluencingFactors.size(),is(underWriterDocumentWithPlan.getUnderWriterInfluencingFactors().size()));
        assertNull(underWriterDocumentWithPlan.getCoverageId());
    }

    @Test
    public void givenAPlanCodeCoverageIdAndUnderWriterInfluencingFactor_whenAllRequiredValuesAreNotNull_thenItShouldCreateTheUnderWriterDocumentWithPlanAndCoverage(){
        UnderWriterDocumentId underWriterDocumentId = new UnderWriterDocumentId("UR001");
        String planCode ="P002";
        CoverageId coverageId = new CoverageId("C002");
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE);
        UnderWriterDocument  underWriterDocumentWithOptionalCoverage  = UnderWriterDocument.createUnderWriterDocumentWithOptionalCoverage(underWriterDocumentId, planCode, coverageId, UnderWriterProcessType.CLAIM, underWriterDocument, underWriterInfluencingFactors, new DateTime("2016-12-31"));
        assertNotNull(underWriterDocumentWithOptionalCoverage);
        assertThat("P002", is(underWriterDocumentWithOptionalCoverage.getPlanCode()));
        assertThat(underWriterInfluencingFactors.size(),is(underWriterDocumentWithOptionalCoverage.getUnderWriterInfluencingFactors().size()));
        assertNotNull(underWriterDocumentWithOptionalCoverage.getCoverageId());
    }

    @Test
    public void givenAExpireDate_thenItShouldAssignTheExpireDateToTheUnderWriterDocument(){
        UnderWriterDocumentId underWriterDocumentId = new UnderWriterDocumentId("UR001");
        String planCode ="P002";
        CoverageId coverageId = new CoverageId("C002");
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE);
        UnderWriterDocument  underWriterDocumentWithOptionalCoverage  = UnderWriterDocument.createUnderWriterDocumentWithOptionalCoverage(underWriterDocumentId, planCode, coverageId, UnderWriterProcessType.CLAIM, underWriterDocument, underWriterInfluencingFactors, new DateTime("2016-12-30"));
        underWriterDocumentWithOptionalCoverage.expireUnderWriterDocument(new DateTime("2017-01-01"));
        assertThat(underWriterDocumentWithOptionalCoverage.getValidTill(),is(new DateTime("2017-01-01")));
    }
}
