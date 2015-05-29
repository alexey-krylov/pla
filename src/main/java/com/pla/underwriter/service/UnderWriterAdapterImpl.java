package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.underwriter.domain.model.UnderWriterLineItem;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.domain.model.UnderWritingRoutingLevelItem;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.underwriter.exception.UnderWriterException.raiseInfluencingFactorMismatchException;
import static com.pla.underwriter.exception.UnderWriterException.raiseUnderWriterNotFoundException;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Admin on 5/27/2015.
 */
@Service
public class UnderWriterAdapterImpl implements IUnderWriterAdapter {

    private UnderWriterFinder underWriterFinder;

    @Autowired
    public UnderWriterAdapterImpl(UnderWriterFinder underWriterFinder) {
        this.underWriterFinder = underWriterFinder;
    }

    @Override
    public String getRoutingLevel(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto) {
        return getTheRoutingLevel(underWriterRoutingLevelDetailDto);
    }

    @Override
    public List<Map<String, Object>> getDocumentsForUnderWriterApproval(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getDocumentsForApproverApproval() {
        return null;
    }

    public String getTheRoutingLevel(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto) {
        UnderWriterRoutingLevel underWriterRoutingLevel = underWriterFinder.findUnderWriterRoutingLevel(underWriterRoutingLevelDetailDto);
        boolean hasAllInfluencingFactor = underWriterRoutingLevel.hasAllInfluencingFactor(transformUnderWriterInfluencingFactor(underWriterRoutingLevelDetailDto.getUnderWriterInfluencingFactor()));
        if (!hasAllInfluencingFactor) {
            raiseInfluencingFactorMismatchException();
        }
        Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems = underWriterRoutingLevel.getUnderWritingRoutingLevelItems();
        UnderWritingRoutingLevelItem underWritingRoutingLevelItem = findUnderWriterRoutingLevelItem(underWritingRoutingLevelItems, underWriterRoutingLevelDetailDto.getUnderWriterInfluencingFactor());
        return underWritingRoutingLevelItem.getRoutingLevel().name();
    }

    public UnderWritingRoutingLevelItem findUnderWriterRoutingLevelItem(Set<UnderWritingRoutingLevelItem> underWriterItems, List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos) {
        List<UnderWritingRoutingLevelItem> underWriterItemList = underWriterItems.stream().filter(new FilterUnderWriterItemPredicate(underWriterInfluencingFactorDetailDtos)).collect(Collectors.toList());
        if (isEmpty(underWriterItemList)) {
            raiseUnderWriterNotFoundException();
        }
        checkArgument(underWriterItemList.size() == 1);
        return underWriterItemList.get(0);
    }

    private class FilterUnderWriterItemPredicate implements Predicate<UnderWritingRoutingLevelItem> {

        private List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos;

        FilterUnderWriterItemPredicate(List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos) {
            this.underWriterInfluencingFactorDetailDtos = underWriterInfluencingFactorDetailDtos;
        }

        @Override
        public boolean test(UnderWritingRoutingLevelItem underWritingRoutingLevelItem) {
            int noOfMatch = 0;
            for (UnderWriterLineItem underWriterLineItem : underWritingRoutingLevelItem.getUnderWriterLineItems()) {
                if (isMatchesInfluencingFactorAndValue(underWriterLineItem, underWriterInfluencingFactorDetailDtos)) {
                    noOfMatch = noOfMatch + 1;
                    continue;
                }
            }
            return underWriterInfluencingFactorDetailDtos.size() == noOfMatch;
        }
    }

    private boolean isMatchesInfluencingFactorAndValue(UnderWriterLineItem underWriterLineItem, List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos) {
        for (UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem underWriterInfluencingFactorDetailDto : underWriterInfluencingFactorDetailDtos) {
            if (underWriterInfluencingFactorDetailDto.getUnderWriterInfluencingFactor().equals(underWriterLineItem.getUnderWriterInfluencingFactor().name())) {
                return underWriterLineItem.getUnderWriterInfluencingFactor().isValueIsInRange(underWriterInfluencingFactorDetailDto.getValue(), underWriterLineItem.getInfluencingItemFrom(), underWriterLineItem.getInfluencingItemTo());
            }
        }
        return false;
    }

      List<String> transformUnderWriterInfluencingFactor(List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorDetailDtos) {
        List<String> underWriterInfluencingFactors = Lists.newArrayList();
        underWriterInfluencingFactorDetailDtos.forEach(influencingFactor -> {
            underWriterInfluencingFactors.add(influencingFactor.getUnderWriterInfluencingFactor());
        });
        return underWriterInfluencingFactors;
    }

}
