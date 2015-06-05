package com.pla.underwriter.application;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.dto.UnderWriterDto;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 6/4/2015.
 */
public class CreateUnderWriterDocumentCommandUnitTest {

    CreateUnderWriterDocumentCommand createUnderWriterDocumentCommand;

    @Before
    public void setUp(){
        createUnderWriterDocumentCommand = new CreateUnderWriterDocumentCommand();
        List<UnderWriterDto> underWriterList  = Lists.newArrayList();
        UnderWriterDto underWriterDto = new UnderWriterDto();
        List<Map<String,Object>> underWriterDocuments = Lists.newArrayList();
        Map<String,Object>  documentMap = Maps.newLinkedHashMap();
        documentMap.put("documentCode","Document_one");
        documentMap.put("documentName","Document One");
        underWriterDocuments.add(documentMap);
        underWriterDto.setUnderWriterDocuments(underWriterDocuments);

        List<Map<String ,Object>> underWriterDocumentLineItem = Lists.newArrayList();
        Map<String ,Object> underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Sum Assured From");
        underWriterLineItemMap.put("influencingItem","1000");
        underWriterDocumentLineItem.add(underWriterLineItemMap);
        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Sum Assured To");
        underWriterLineItemMap.put("influencingItem","2000");
        underWriterDocumentLineItem.add(underWriterLineItemMap);

        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Age From");
        underWriterLineItemMap.put("influencingItem","10");
        underWriterDocumentLineItem.add(underWriterLineItemMap);
        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Age To");
        underWriterLineItemMap.put("influencingItem","20");
        underWriterDocumentLineItem.add(underWriterLineItemMap);
        underWriterDto.setUnderWriterDocumentLineItem(underWriterDocumentLineItem);
        underWriterList.add(underWriterDto);

        underWriterDto = new UnderWriterDto();
        underWriterDocuments = Lists.newArrayList();
        documentMap = Maps.newLinkedHashMap();
        documentMap.put("documentCode","Document_Two");
        documentMap.put("documentName","Document Two");
        underWriterDocuments.add(documentMap);
        underWriterDto.setUnderWriterDocuments(underWriterDocuments);

        underWriterDocumentLineItem = Lists.newArrayList();
        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Sum Assured From");
        underWriterLineItemMap.put("influencingItem","4000");
        underWriterDocumentLineItem.add(underWriterLineItemMap);
        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Sum Assured To");
        underWriterLineItemMap.put("influencingItem","5000");
        underWriterDocumentLineItem.add(underWriterLineItemMap);

        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Age From");
        underWriterLineItemMap.put("influencingItem","40");
        underWriterDocumentLineItem.add(underWriterLineItemMap);
        underWriterLineItemMap = Maps.newLinkedHashMap();
        underWriterLineItemMap.put("underWriterInfluencingFactor","Age To");
        underWriterLineItemMap.put("influencingItem","50");
        underWriterDocumentLineItem.add(underWriterLineItemMap);
        underWriterDto.setUnderWriterDocumentLineItem(underWriterDocumentLineItem);
        underWriterList.add(underWriterDto);
        createUnderWriterDocumentCommand.setUnderWriterDocumentItems(underWriterList);
        createUnderWriterDocumentCommand.setUnderWriterInfluencingFactors(Lists.newArrayList(UnderWriterInfluencingFactor.AGE,UnderWriterInfluencingFactor.SUM_ASSURED));

    }

    @Test
    public void givenUnderWriterDocumentSetUp_thenItShouldTransformSetUpAndReturnTheUnderWriterDocument(){
        List<UnderWriterDto> underWriterList = createUnderWriterDocumentCommand.transformTheUnderWriterDocumentLineItem();
        assertNotNull(underWriterList);
        assertThat(underWriterList.size(),is(2));
    }


}
