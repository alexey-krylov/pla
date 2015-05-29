package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.underwriter.domain.model.RoutingLevel;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterLineItem;
import com.pla.underwriter.domain.model.UnderWritingRoutingLevelItem;
import com.pla.underwriter.exception.UnderWriterException;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Admin on 5/29/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnderWriterAdapterImplUnitTest {

    @Mock
    private UnderWriterFinder underWriterFinder;

    UnderWriterAdapterImpl underWriterAdapter;

    Set<UnderWritingRoutingLevelItem> underWriterItems;

    List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos;


    @Before
    public void setUp(){
        underWriterAdapter = new UnderWriterAdapterImpl(underWriterFinder);
        underWriterItems= Sets.newLinkedHashSet();
        Set<UnderWriterLineItem> underWriterLineItems = Sets.newLinkedHashSet();
        UnderWriterLineItem underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.AGE,"10","20");
        underWriterLineItems.add(underWriterLineItem);

        underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.SUM_ASSURED,"1000","2000");
        underWriterLineItems.add(underWriterLineItem);

        underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.BMI,"5","8");
        underWriterLineItems.add(underWriterLineItem);

        underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.HEIGHT,"170","180");
        underWriterLineItems.add(underWriterLineItem);

        underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.WEIGHT,"70","80");
        underWriterLineItems.add(underWriterLineItem);

        underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.CLAIM_AMOUNT,"4000","8000");
        underWriterLineItems.add(underWriterLineItem);

        underWriterInfluencingFactorDetailDtos = Lists.newArrayList();
        UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem  underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(),"10");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(),"1000");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.BMI.name(),"6");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.HEIGHT.name(),"178");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.WEIGHT.name(),"76");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.CLAIM_AMOUNT.name(),"7000");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);

        UnderWritingRoutingLevelItem underWritingRoutingLevelItem = new UnderWritingRoutingLevelItem(RoutingLevel.UNDERWRITING_LEVEL_ONE,underWriterLineItems);
        underWriterItems.add(underWritingRoutingLevelItem);

    }



    @Test
    public void
    givenUnderWriterInfluencingFactorItem_whenInfluencingFactorAreInRangeWithUnderWriterRoutingLevelInfluencingFactor_thenItShouldReturnTheUnderWriterRoutingLevelLineItem(){
        UnderWritingRoutingLevelItem underWritingRoutingLevelItem  =  underWriterAdapter.findUnderWriterRoutingLevelItem(underWriterItems, underWriterInfluencingFactorDetailDtos);
        assertThat(RoutingLevel.UNDERWRITING_LEVEL_ONE,is(underWritingRoutingLevelItem.getRoutingLevel()));
        assertThat(underWritingRoutingLevelItem.getUnderWriterLineItems().size(),is(6));

    }

    @Test(expected = UnderWriterException.class)
    public void
    givenUnderWriterInfluencingFactorItem_whenInfluencingFactorAreNotInRangeWithUnderWriterRoutingLevelInfluencingFactor_thenItShouldThrowUnderWriterException(){
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos =Lists.newArrayList();
        UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem  underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(),"40");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(),"2000");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        UnderWritingRoutingLevelItem underWritingRoutingLevelItem  =  underWriterAdapter.findUnderWriterRoutingLevelItem(underWriterItems, underWriterInfluencingFactorDetailDtos);
        assertThat(RoutingLevel.UNDERWRITING_LEVEL_ONE,is(underWritingRoutingLevelItem.getRoutingLevel()));
        assertThat(underWritingRoutingLevelItem.getUnderWriterLineItems().size(),is(2));

    }

    @Test
    public void givenUnderWriterInfluencingFactorItem_thenItShouldTransformAndReturnTheListOfUnderWriterInfluencingFactor(){
        List<String > underWriterInfluencingFactors = underWriterAdapter.transformUnderWriterInfluencingFactor(underWriterInfluencingFactorDetailDtos);
        assertNotNull(underWriterInfluencingFactors);
        assertThat(underWriterInfluencingFactors.size(),is(6));
    }
}
