package com.pla.underwriter.domain.model;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.common.AppConstants;

import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnderWritingRoutingLevelItem {

    private RoutingLevel routingLevel;

    private Set<UnderWriterLineItem> underWriterLineItems;

    public static UnderWritingRoutingLevelItem create(Map<Object, Map<String, Object>> underWriterRoutingLevelMap) {
        UnderWritingRoutingLevelItem underWritingRoutingLevelItem = new UnderWritingRoutingLevelItem();
        Set<UnderWriterLineItem> listOfUnderWriterLineItem = Sets.newLinkedHashSet();
        for (Map.Entry<Object, Map<String, Object>> underWriterMap : underWriterRoutingLevelMap.entrySet()) {
            if (AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME.equals(underWriterMap.getKey())) {
                for (RoutingLevel routingLevel : RoutingLevel.values()) {
                    if (routingLevel.getDescription().equals(underWriterMap.getValue().get(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME)))
                        underWritingRoutingLevelItem.setRoutingLevel(routingLevel);
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
