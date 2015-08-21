package com.pla.underwriter.dto;

import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnderWriterLineItemDto {

    private UnderWriterInfluencingFactor underWriterInfluencingFactor;
    private String influencingItemFrom;
    private String influencingItemTo;

    public static UnderWriterLineItemDto getUnderWriterDocumentLineItem(List<Map<String, Object>> underWriterLineItemList, UnderWriterInfluencingFactor underWriterInfluencingFactor){
        Map<String, Object> underWriterDocumentLineItemMap =  underWriterInfluencingFactor.getTheUnderWriterDocumentLineItemByType(underWriterLineItemList);
        String fromValue = underWriterInfluencingFactor.getRoutingLevelInfluencingFactorFromValue(underWriterDocumentLineItemMap);
        String toValue = underWriterInfluencingFactor.getInfluencingFactorToValue(underWriterDocumentLineItemMap);
        return new UnderWriterLineItemDto(underWriterInfluencingFactor,fromValue,toValue);
    }


}
