package com.pla.underwriter.domain.model;

import com.google.common.collect.Lists;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
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
@Document(collection = "under_writing_router")
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"underWriterRoutingLevelId", "planId"})
public class UnderWriterRoutingLevel {

    @Id
    private UnderWriterRoutingLevelId underWriterRoutingLevelId;

    @Getter
    private String planCode;

    @Getter
    private CoverageId coverageId;

    private UnderWriterProcessType processType;

    @Getter
    private Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    private DateTime effectiveFrom;

    private DateTime validTill;


    private UnderWriterRoutingLevel(UnderWriterRoutingLevelId underWriterRoutingLevelId, String planCode, UnderWriterProcessType processType, Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors, DateTime effectiveFrom) {
        checkArgument(underWriterRoutingLevelId != null);
        checkArgument(planCode != null);
        checkArgument(effectiveFrom != null);
        checkArgument(isNotEmpty(underWritingRoutingLevelItems));
        checkArgument(isNotEmpty(underWriterInfluencingFactors));
        this.underWriterRoutingLevelId = underWriterRoutingLevelId;
        this.planCode = planCode;
        this.processType  = processType;
        this.underWritingRoutingLevelItems = underWritingRoutingLevelItems;
        this.underWriterInfluencingFactors=  underWriterInfluencingFactors;
        this.effectiveFrom = effectiveFrom;
    }


    public static UnderWriterRoutingLevel createUnderWriterRoutingLevelWithPlan(UnderWriterRoutingLevelId underWriterRoutingLevelId,String planCode, UnderWriterProcessType processType,  List<Map<Object, Map<String, Object>>>  underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,DateTime effectiveFrom) {
        Set<UnderWritingRoutingLevelItem>  underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        return new UnderWriterRoutingLevel(underWriterRoutingLevelId,planCode,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
    }

    public static UnderWriterRoutingLevel createUnderWriterRoutingLevelWithOptionalCoverage(UnderWriterRoutingLevelId underWriterRoutingLevelId ,String planCode, CoverageId coverageId, UnderWriterProcessType processType, List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,DateTime effectiveFrom) {
        Set<UnderWritingRoutingLevelItem> underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        UnderWriterRoutingLevel underWriterDocument =  new UnderWriterRoutingLevel(underWriterRoutingLevelId,planCode,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
        underWriterDocument.coverageId = coverageId;
        return underWriterDocument;
    }

    public static Set<UnderWritingRoutingLevelItem> withUnderWritingLevelItem(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWritingRoutingLevelItem> writingRoutingLevelItems = transformUnderWriterRoutingLevelData(underWriterRoutingLevelDataFromExcel);
        return writingRoutingLevelItems;
    }

    public UnderWriterRoutingLevel expireUnderWriterRoutingLevel(DateTime validTill) {
        this.validTill  = validTill;
        return this;
    }

    public static Set<UnderWritingRoutingLevelItem> transformUnderWriterRoutingLevelData(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWritingRoutingLevelItem> listOfUnderWriterRoutingLevelData = underWriterRoutingLevelDataFromExcel.parallelStream().map(new TransformUnderWriterRoutingLevel()).collect(Collectors.toSet());
        return listOfUnderWriterRoutingLevelData;
    }

    public static class TransformUnderWriterRoutingLevel implements Function<Map<Object, Map<String, Object>>, UnderWritingRoutingLevelItem> {
        @Override
        public UnderWritingRoutingLevelItem apply(Map<Object, Map<String, Object>> underWriterRoutingLevelMap) {
            UnderWritingRoutingLevelItem underWritingRoutingLevelItem = UnderWritingRoutingLevelItem.create(underWriterRoutingLevelMap);
            return underWritingRoutingLevelItem;
        }
    }

    public boolean hasAllInfluencingFactor(List<String> underWriterInfluencingFactor) {
        List<UnderWriterInfluencingFactor> underWriterInfluencingFactorsList = Lists.newArrayList();
        underWriterInfluencingFactor.forEach(influencingFactor->{
            underWriterInfluencingFactorsList.add(UnderWriterInfluencingFactor.valueOf(influencingFactor));
        });
        return this.underWriterInfluencingFactors.containsAll(underWriterInfluencingFactorsList);
    }
}
