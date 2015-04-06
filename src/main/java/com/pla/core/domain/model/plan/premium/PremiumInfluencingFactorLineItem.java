/*
 * Copyright (c) 3/25/15 8:57 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.plan.premium;

import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode
class PremiumInfluencingFactorLineItem {

    private PremiumInfluencingFactor premiumInfluencingFactor;

    private String value;

    private PremiumInfluencingFactorLineItem(PremiumInfluencingFactor premiumInfluencingFactor, String value) {
        this.premiumInfluencingFactor = premiumInfluencingFactor;
        this.value = value;
    }

    public static Set<PremiumInfluencingFactorLineItem> createPremiumInfluencingFactor(Map<PremiumInfluencingFactor, String> premiumInfluencingFactorValueMap) {
        Set<PremiumInfluencingFactorLineItem> premiumInfluencingFactorsSetLineItem = premiumInfluencingFactorValueMap.entrySet().stream().map(new TransformToPremiumInfluencingFactor()).collect(Collectors.toSet());
        return premiumInfluencingFactorsSetLineItem;
    }


    private static class TransformToPremiumInfluencingFactor implements Function<Map.Entry<PremiumInfluencingFactor, String>, PremiumInfluencingFactorLineItem> {


        @Override
        public PremiumInfluencingFactorLineItem apply(Map.Entry<PremiumInfluencingFactor, String> entry) {
            return new PremiumInfluencingFactorLineItem(entry.getKey(), entry.getValue());
        }
    }

}
