package com.pla.underwriter.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 4/27/2015.
 */
@Document(collection = "under_writer_document")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"underWriterDocumentId", "planId"})
public class UnderWriterDocument {

    @Id
    private UnderWriterDocumentId underWriterDocumentId;

    private PlanId planId;

    private CoverageId coverageId;

    private UnderWriterProcessType processType;

    private Set<UnderWriterDocumentItem> underWriterDocumentItems;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    private DateTime effectiveFrom;

    private DateTime validTill;


    private UnderWriterDocument(UnderWriterDocumentId underWriterDocumentId,PlanId planId,UnderWriterProcessType processType,Set<UnderWriterDocumentItem> underWriterDocumentItems,List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,DateTime effectiveFrom) {
        checkArgument(underWriterDocumentId != null);
        checkArgument(planId != null);
        checkArgument(effectiveFrom != null);
        checkArgument(isNotEmpty(underWriterDocumentItems));
        checkArgument(isNotEmpty(underWriterInfluencingFactors));
        this.underWriterDocumentId = underWriterDocumentId;
        this.planId = planId;
        this.processType  = processType;
        this.underWriterDocumentItems = underWriterDocumentItems;
        this.underWriterInfluencingFactors=  underWriterInfluencingFactors;
        this.effectiveFrom = effectiveFrom;
    }


    public static UnderWriterDocument createUnderWriterDocumentWithPlan(UnderWriterDocumentId underWriterDocumentId,PlanId planId, UnderWriterProcessType processType,List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,DateTime effectiveFrom) {
        Set<UnderWriterDocumentItem>  underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        return new UnderWriterDocument(underWriterDocumentId,planId,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
    }

    public static UnderWriterDocument createUnderWriterDocumentWithOptionalCoverage(UnderWriterDocumentId underWriterDocumentId ,PlanId  planId, CoverageId coverageId, UnderWriterProcessType processType, List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,DateTime effectiveFrom) {
        Set<UnderWriterDocumentItem>  underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        UnderWriterDocument underWriterDocument =  new UnderWriterDocument(underWriterDocumentId,planId,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
        underWriterDocument.coverageId = coverageId;
        return underWriterDocument;
    }

    public static Set<UnderWriterDocumentItem> withUnderWritingLevelItem(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWriterDocumentItem> underWriterLineItems = transformUnderWriterRoutingLevelData(underWriterRoutingLevelDataFromExcel);
        return underWriterLineItems;
    }

    public UnderWriterDocument expireUnderWriterDocument(DateTime validTill) {
        this.validTill  = validTill;
        return this;
    }


    public static Set<UnderWriterDocumentItem> transformUnderWriterRoutingLevelData(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWriterDocumentItem> listOfUnderWriterRoutingLevelData = underWriterRoutingLevelDataFromExcel.parallelStream().map(new TransformUnderWriterRoutingLevel()).collect(Collectors.toSet());
        return listOfUnderWriterRoutingLevelData;
    }

    public boolean hasAllInfluencingFactor(List<String> underWriterInfluencingFactor) {
        return underWriterInfluencingFactor.containsAll(this.underWriterInfluencingFactors.parallelStream().map(each -> each.name()).collect(Collectors.toList()));
    }

    public static class TransformUnderWriterRoutingLevel implements Function<Map<Object, Map<String, Object>>, UnderWriterDocumentItem> {
        @Override
        public UnderWriterDocumentItem apply(Map<Object, Map<String, Object>> underWriterRoutingLevelMap) {
            UnderWriterDocumentItem underWritingRoutingLevelItem = UnderWriterDocumentItem.create(underWriterRoutingLevelMap);
            return underWritingRoutingLevelItem;
        }
    }

    public List<Map<String,Object>> transformUnderWriterDocumentLineItem(){
        return this.underWriterDocumentItems.stream().map(new Function<UnderWriterDocumentItem, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(UnderWriterDocumentItem underWriterDocumentItem) {
                Map<String, Object> underWriterItemMap = Maps.newLinkedHashMap();
                List<Map<String, Object>> documentLineItemList = Lists.newArrayList();
                underWriterDocumentItem.getUnderWriterLineItems().stream().map(new Function<UnderWriterLineItem, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(UnderWriterLineItem underWriterLineItem) {
                        Map<String, Object> underWriterLineItems = underWriterLineItem.getUnderWriterInfluencingFactor().transformUnderWriterDocumentLineItem(underWriterLineItem.getInfluencingItemFrom(), underWriterLineItem.getInfluencingItemTo());
                        for (Map.Entry<String, Object> underWriterLineItemMap : underWriterLineItems.entrySet()) {
                            Map<String, Object> underWriterItemMap = Maps.newLinkedHashMap();
                            underWriterItemMap.put("underWriterInfluencingFactor", underWriterLineItemMap.getKey());
                            underWriterItemMap.put("influencingItem", underWriterLineItemMap.getValue());
                            documentLineItemList.add(underWriterItemMap);
                        }
                        return underWriterItemMap;
                    }
                }).collect(Collectors.toList());
                underWriterItemMap.put("underWriterDocumentLineItem", documentLineItemList);
                underWriterItemMap.put("underWriterDocuments", underWriterDocumentItem.getDocumentIds());
                return underWriterItemMap;
            }
        }).collect(Collectors.toList());
    }

}
