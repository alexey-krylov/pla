package com.pla.underwriter.dto;

import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
public class UnderWriterDto {

    private String routingLevel;
    private List<Map<String ,Object>> underWriterDocumentLineItem;
    private List<UnderWriterLineItemDto> underWriterLineItem;
    private List<Map<String,Object>> underWriterDocuments;
    private Set<String> documents;

    public void transformUnderWriterDocument(List<Map<String,Object>> underWriterDocuments){
        this.documents = transformDocument(underWriterDocuments);
    }

    public void setUnderWriterLineItem(List<Map<String ,Object>> underWriterDocumentLineItem,List<UnderWriterInfluencingFactor> underWriterInfluencingFactor){
        this.underWriterLineItem  = groupByTheLineItemByInfluencingFactor(underWriterDocumentLineItem,underWriterInfluencingFactor);
    }

    private Set<String> transformDocument(List<Map<String,Object>> underWriterDocuments){
        Set<String> document = underWriterDocuments.stream().map(new DocumentTransformer()).collect(Collectors.toSet());
        return document;
    }

    private class DocumentTransformer implements Function<Map<String, Object>, String> {
        @Override
        public String apply(Map<String, Object> documentMap) {
            return documentMap.get("documentCode").toString();
        }
    }

    public List<UnderWriterLineItemDto> groupByTheLineItemByInfluencingFactor(List<Map<String ,Object>> underWriterDocumentLineItem,List<UnderWriterInfluencingFactor> underWriterInfluencingFactor){
        List<UnderWriterLineItemDto> underWriterLineItemList =  underWriterInfluencingFactor.stream().map(new UnderWriterInfluencingFactorTransformer(underWriterDocumentLineItem)).collect(Collectors.toList());
        return underWriterLineItemList;
    }

    private class UnderWriterInfluencingFactorTransformer implements Function<UnderWriterInfluencingFactor,UnderWriterLineItemDto > {

        List<Map<String,Object>> underWriterDocumentLineItem;

        public UnderWriterInfluencingFactorTransformer(List<Map<String, Object>> underWriterDocumentLineItem) {
            this.underWriterDocumentLineItem  = underWriterDocumentLineItem;
        }

        @Override
        public UnderWriterLineItemDto apply(UnderWriterInfluencingFactor influencingFactor) {
            return UnderWriterLineItemDto.getUnderWriterDocumentLineItem(underWriterDocumentLineItem, influencingFactor);
        }
    }
}
