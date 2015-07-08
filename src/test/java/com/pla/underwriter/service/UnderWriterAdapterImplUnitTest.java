package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.client.domain.model.Client;
import com.pla.client.domain.model.ClientBuilder;
import com.pla.client.repository.ClientRepository;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
import com.pla.underwriter.domain.model.*;
import com.pla.underwriter.exception.UnderWriterException;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.common.AppConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 5/29/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnderWriterAdapterImplUnitTest {

    @Mock
    private UnderWriterFinder underWriterFinder;

    @Mock
    private ClientRepository clientRepository;

    private UnderWriterAdapterImpl underWriterAdapter;

    private Set<UnderWritingRoutingLevelItem> underWriterItems;

    private List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos;
    private UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto;

    List<Map<Object,Map<String,Object>>> underWriterDocumentItem;

    @Before
    public void setUp(){
        underWriterAdapter = new UnderWriterAdapterImpl(underWriterFinder,clientRepository);
        underWriterItems= Sets.newLinkedHashSet();
        Set<UnderWriterLineItem> underWriterLineItems = Sets.newLinkedHashSet();
        UnderWriterLineItem underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.AGE,"10","20");
        underWriterLineItems.add(underWriterLineItem);

        underWriterLineItem = new UnderWriterLineItem(UnderWriterInfluencingFactor.SUM_ASSURED,"1000","2000");
        underWriterLineItems.add(underWriterLineItem);

        underWriterInfluencingFactorDetailDtos = Lists.newArrayList();
        UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem  underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(),"10");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(),"1000");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);


        UnderWritingRoutingLevelItem underWritingRoutingLevelItem = new UnderWritingRoutingLevelItem(RoutingLevel.UNDERWRITING_LEVEL_ONE,underWriterLineItems);
        underWriterItems.add(underWritingRoutingLevelItem);

        underWriterRoutingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId("P001"),new LocalDate("2015-01-01"),"CLAIM");
        underWriterRoutingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorDetailDtos);


        underWriterDocumentItem = Lists.newArrayList();
        Map<Object,Map<String,Object>> underWriterLineItems1 = Maps.newLinkedHashMap();
        Map<String,Object> underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("Age From","10");
        underWriterItem.put("Age To","20");
        underWriterLineItems1.put(UnderWriterInfluencingFactor.AGE,underWriterItem);

        underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put("Sum Assured From","3000");
        underWriterItem.put("Sum Assured To","4000");
        underWriterLineItems1.put(UnderWriterInfluencingFactor.SUM_ASSURED,underWriterItem);



        underWriterItem = Maps.newLinkedHashMap();
        underWriterItem.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,"UnderWriting level 2");
        underWriterLineItems1.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,underWriterItem);
        underWriterDocumentItem.add(underWriterLineItems1);

    }



    @Test
    public void
    givenUnderWriterInfluencingFactorItem_whenInfluencingFactorAreInRangeWithUnderWriterRoutingLevelInfluencingFactor_thenItShouldReturnTheUnderWriterRoutingLevelLineItem(){
        UnderWritingRoutingLevelItem underWritingRoutingLevelItem  =  underWriterAdapter.findUnderWriterRoutingLevelItem(underWriterItems, underWriterInfluencingFactorDetailDtos);
        assertThat(RoutingLevel.UNDERWRITING_LEVEL_ONE,is(underWritingRoutingLevelItem.getRoutingLevel()));
        assertThat(underWritingRoutingLevelItem.getUnderWriterLineItems().size(),is(2));

    }

    @Test(expected = UnderWriterException.class)
    public void
    givenUnderWriterInfluencingFactorItem_whenInfluencingFactorAreNotInRangeWithUnderWriterRoutingLevelInfluencingFactor_thenItShouldThrowUnderWriterException(){
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos =Lists.newArrayList();
        UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem  underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(),"30");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        underWriterInfluencingFactorItem = new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(),"3000");
        underWriterInfluencingFactorDetailDtos.add(underWriterInfluencingFactorItem);
        UnderWritingRoutingLevelItem underWritingRoutingLevelItem  =  underWriterAdapter.findUnderWriterRoutingLevelItem(underWriterItems, underWriterInfluencingFactorDetailDtos);
        assertNull(underWritingRoutingLevelItem);
    }

    @Test
    public void givenUnderWriterInfluencingFactorItem_thenItShouldTransformAndReturnTheListOfUnderWriterInfluencingFactor(){
        List<String > underWriterInfluencingFactors = underWriterAdapter.transformUnderWriterInfluencingFactor(underWriterInfluencingFactorDetailDtos);
        assertNotNull(underWriterInfluencingFactors);
        assertThat(underWriterInfluencingFactors.size(),is(2));
    }

    @Test
    public void givenInfluencingFactorCombination_whenNoRoutingLevelFound_thenItShouldCheckAgainstTheClientIdAndReturnTheRoutingLevel(){
        ClientBuilder clientBuilder = new ClientBuilder("Client One");
        List<ClientDetailDto.ClientDocumentDetailDto> clientDocument =  Lists.newArrayList();
        ClientDetailDto clientDetailDto  = new ClientDetailDto();
        ClientDetailDto.ClientDocumentDetailDto clientDocumentDetailDto =   clientDetailDto.new ClientDocumentDetailDto();
        clientDocumentDetailDto.setRoutingLevel("UNDERWRITING_LEVEL_TWO");
        clientDocumentDetailDto.setDocumentName("D_ONE");
        clientDocumentDetailDto.setDocumentType("UNDERWRITER");
        clientDocument.add(clientDocumentDetailDto);

        clientDocumentDetailDto =   clientDetailDto.new ClientDocumentDetailDto();
        clientDocumentDetailDto.setDocumentName("D_ONE");
        clientDocumentDetailDto.setDocumentType("MANDATORY");
        clientDocument.add(clientDocumentDetailDto);

        clientBuilder.withClientDocument(clientDocument);
        Client client = Client.createClient(clientBuilder, "CL001");
        client.withClientDocument(clientBuilder.getClientDocuments());

        UnderWriterRoutingLevelId underWriterRoutingLevelId = new UnderWriterRoutingLevelId("UR001");
        String planCode = "P001";
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactors = Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.SUM_ASSURED,UnderWriterInfluencingFactor.BMI,
                UnderWriterInfluencingFactor.HEIGHT,UnderWriterInfluencingFactor.WEIGHT,UnderWriterInfluencingFactor.CLAIM_AMOUNT);
        UnderWriterRoutingLevel underWriterRoutingLevel = UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithPlan(underWriterRoutingLevelId, planCode, UnderWriterProcessType.CLAIM, underWriterDocumentItem, underWriterInfluencingFactors, new LocalDate("2016-12-31"));
        when(underWriterFinder.findUnderWriterRoutingLevel(underWriterRoutingLevelDetailDto)).thenReturn(underWriterRoutingLevel);
        when(clientRepository.findOne(anyObject())).thenReturn(client);
        RoutingLevel routingLevel =  underWriterAdapter.getRoutingLevel(underWriterRoutingLevelDetailDto);
        assertThat(RoutingLevel.UNDERWRITING_LEVEL_TWO,is(routingLevel));
    }
}
