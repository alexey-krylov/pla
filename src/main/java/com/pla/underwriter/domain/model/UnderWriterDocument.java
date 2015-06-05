package com.pla.underwriter.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import lombok.*;
import org.joda.time.LocalDate;
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
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"underWriterDocumentId", "planId"})
public class UnderWriterDocument {

    @Id
    private UnderWriterDocumentId underWriterDocumentId;

    @Getter
    private String planCode;

    @Getter
    private CoverageId coverageId;

    private UnderWriterProcessType processType;

    private  Set<UnderWriterDocumentItem> underWriterDocumentItems;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    private LocalDate effectiveFrom;

    private LocalDate validTill;


    private UnderWriterDocument(UnderWriterDocumentId underWriterDocumentId,String planCode,UnderWriterProcessType processType,Set<UnderWriterDocumentItem> underWriterDocumentItems,List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,LocalDate effectiveFrom) {
        checkArgument(underWriterDocumentId != null);
        checkArgument(planCode != null);
        checkArgument(effectiveFrom != null);
        checkArgument(isNotEmpty(underWriterDocumentItems));
        checkArgument(isNotEmpty(underWriterInfluencingFactors));
        this.underWriterDocumentId = underWriterDocumentId;
        this.planCode = planCode;
        this.processType  = processType;
        this.underWriterDocumentItems = underWriterDocumentItems;
        this.underWriterInfluencingFactors=  underWriterInfluencingFactors;
        this.effectiveFrom = effectiveFrom;
    }


    public static UnderWriterDocument createUnderWriterDocumentWithPlan(UnderWriterDocumentId underWriterDocumentId,String planCode, UnderWriterProcessType processType,List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,LocalDate effectiveFrom) {
        Set<UnderWriterDocumentItem>  underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        return new UnderWriterDocument(underWriterDocumentId,planCode,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
    }

    public static UnderWriterDocument createUnderWriterDocumentWithOptionalCoverage(UnderWriterDocumentId underWriterDocumentId ,String  planCode, CoverageId coverageId, UnderWriterProcessType processType, List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,LocalDate effectiveFrom) {
        Set<UnderWriterDocumentItem>  underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        UnderWriterDocument underWriterDocument =  new UnderWriterDocument(underWriterDocumentId,planCode,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
        underWriterDocument.coverageId = coverageId;
        return underWriterDocument;
    }

    public static Set<UnderWriterDocumentItem> withUnderWritingLevelItem(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWriterDocumentItem> underWriterLineItems = transformUnderWriterRoutingLevelData(underWriterRoutingLevelDataFromExcel);
        return underWriterLineItems;
    }

    public UnderWriterDocument expireUnderWriterDocument(LocalDate validTill) {
        this.validTill  = validTill;
        return this;
    }


    public static Set<UnderWriterDocumentItem> transformUnderWriterRoutingLevelData(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWriterDocumentItem> listOfUnderWriterRoutingLevelData = underWriterRoutingLevelDataFromExcel.parallelStream().map(new TransformUnderWriterRoutingLevel()).collect(Collectors.toSet());
        return listOfUnderWriterRoutingLevelData;
    }

    public static class TransformUnderWriterRoutingLevel implements Function<Map<Object, Map<String, Object>>, UnderWriterDocumentItem> {
        @Override
        public UnderWriterDocumentItem apply(Map<Object, Map<String, Object>> underWriterRoutingLevelMap) {
            UnderWriterDocumentItem underWritingRoutingLevelItem = UnderWriterDocumentItem.create(underWriterRoutingLevelMap);
            return underWritingRoutingLevelItem;
        }
    }

}
