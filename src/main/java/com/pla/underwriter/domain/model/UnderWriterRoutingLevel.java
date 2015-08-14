package com.pla.underwriter.domain.model;

import com.google.common.collect.Lists;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
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
@Document(collection = "under_writing_router")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"underWriterRoutingLevelId", "planId"})
public class UnderWriterRoutingLevel {

    @Id
    private UnderWriterRoutingLevelId underWriterRoutingLevelId;

    private PlanId planId;

    private CoverageId coverageId;

    private UnderWriterProcessType processType;

    private Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    private LocalDate effectiveFrom;

    private LocalDate validTill;


    private UnderWriterRoutingLevel(UnderWriterRoutingLevelId underWriterRoutingLevelId, PlanId planId, UnderWriterProcessType processType, Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors, LocalDate effectiveFrom) {
        checkArgument(underWriterRoutingLevelId != null);
        checkArgument(planId != null);
        checkArgument(effectiveFrom != null);
        checkArgument(isNotEmpty(underWritingRoutingLevelItems));
        checkArgument(isNotEmpty(underWriterInfluencingFactors));
        this.underWriterRoutingLevelId = underWriterRoutingLevelId;
        this.planId = planId;
        this.processType  = processType;
        this.underWritingRoutingLevelItems = underWritingRoutingLevelItems;
        this.underWriterInfluencingFactors=  underWriterInfluencingFactors;
        this.effectiveFrom = effectiveFrom;
    }


    public static UnderWriterRoutingLevel createUnderWriterRoutingLevelWithPlan(UnderWriterRoutingLevelId underWriterRoutingLevelId,PlanId planId, UnderWriterProcessType processType,  List<Map<Object, Map<String, Object>>>  underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,LocalDate effectiveFrom) {
        Set<UnderWritingRoutingLevelItem>  underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        return new UnderWriterRoutingLevel(underWriterRoutingLevelId,planId,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
    }

    public static UnderWriterRoutingLevel createUnderWriterRoutingLevelWithOptionalCoverage(UnderWriterRoutingLevelId underWriterRoutingLevelId ,PlanId planId, CoverageId coverageId, UnderWriterProcessType processType, List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,LocalDate effectiveFrom) {
        Set<UnderWritingRoutingLevelItem> underWriterDocumentItems =  withUnderWritingLevelItem(underWriterRoutingLevelDataFromExcel);
        UnderWriterRoutingLevel underWriterDocument =  new UnderWriterRoutingLevel(underWriterRoutingLevelId,planId,processType,underWriterDocumentItems,underWriterInfluencingFactors,effectiveFrom);
        underWriterDocument.coverageId = coverageId;
        return underWriterDocument;
    }

    public static Set<UnderWritingRoutingLevelItem> withUnderWritingLevelItem(List<Map<Object, Map<String, Object>>> underWriterRoutingLevelDataFromExcel) {
        Set<UnderWritingRoutingLevelItem> writingRoutingLevelItems = transformUnderWriterRoutingLevelData(underWriterRoutingLevelDataFromExcel);
        return writingRoutingLevelItems;
    }

    public UnderWriterRoutingLevel expireUnderWriterRoutingLevel(LocalDate validTill) {
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
