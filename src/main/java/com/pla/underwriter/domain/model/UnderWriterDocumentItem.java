package com.pla.underwriter.domain.model;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.nthdimenzion.common.AppConstants;

import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
public class UnderWriterDocumentItem {

    private Set<String> documents;

    private Set<UnderWriterLineItem> underWriterLineItems;

    public static UnderWriterDocumentItem create(Map<Object, Map<String, Object>> underWriterRoutingLevelMap) {
        UnderWriterDocumentItem underWritingRoutingLevelItem = new UnderWriterDocumentItem();
        Set<UnderWriterLineItem> listOfUnderWriterLineItem = Sets.newLinkedHashSet();
        for (Map.Entry<Object, Map<String, Object>> underWriterMap : underWriterRoutingLevelMap.entrySet()) {
            if (AppConstants.UNDER_WRITER_DOCUMENT.equals(underWriterMap.getKey())) {
                for (Map.Entry<String, Object> underWritingMap : underWriterMap.getValue().entrySet()){
                    if (underWritingMap.getKey().equals(AppConstants.UNDER_WRITER_DOCUMENT)){
                        underWritingRoutingLevelItem.setDocuments((Set)underWritingMap.getValue());
                    }
                }
            } else {
                UnderWriterLineItem underWriterLineItem = new UnderWriterLineItem();
                underWriterLineItem.setUnderWriterInfluencingFactor((UnderWriterInfluencingFactor) underWriterMap.getKey());
                underWriterLineItem.setInfluencingItemFrom(((UnderWriterInfluencingFactor) underWriterMap.getKey()).getRoutingLevelInfluencingFactorFromValue(underWriterMap.getValue()));
                underWriterLineItem.setInfluencingItemTo(((UnderWriterInfluencingFactor) underWriterMap.getKey()).getInfluencingFactorToValue(underWriterMap.getValue()));
                listOfUnderWriterLineItem.add(underWriterLineItem);
            }
        }
        underWritingRoutingLevelItem.setUnderWriterLineItems(listOfUnderWriterLineItem);
        return underWritingRoutingLevelItem;
    }
}
