/*
 * Copyright (c) 3/26/15 7:53 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.plan.premium;

import com.pla.sharedkernel.domain.model.PremiumInfluencingFactor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
@ValueObject
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
class PremiumItem {

    private BigDecimal premium;

    private Set<PremiumInfluencingFactorLineItem> premiumInfluencingFactorLineItems;

    private PremiumItem(Set<PremiumInfluencingFactorLineItem> premiumInfluencingFactorLineItems, BigDecimal premium) {
        this.premiumInfluencingFactorLineItems = premiumInfluencingFactorLineItems;
        this.premium = premium;
    }

    public static PremiumItem createCoveragePremiumItem(Map<Map<PremiumInfluencingFactor, String>, Double> premiumLineItemMap) {
        Set<Map.Entry<Map<PremiumInfluencingFactor, String>, Double>> premiumLineItemEntries = premiumLineItemMap.entrySet();
        Map.Entry<Map<PremiumInfluencingFactor, String>, Double> premiumLineItemEntry = premiumLineItemEntries.iterator().next();
        Set<PremiumInfluencingFactorLineItem> premiumInfluencingFactorLineItems = PremiumInfluencingFactorLineItem.createPremiumInfluencingFactor(premiumLineItemEntry.getKey());
        return new PremiumItem(premiumInfluencingFactorLineItems, BigDecimal.valueOf(premiumLineItemEntry.getValue()));
    }

}
