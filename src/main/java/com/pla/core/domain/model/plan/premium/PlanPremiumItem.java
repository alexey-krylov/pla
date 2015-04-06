/*
 * Copyright (c) 3/25/15 8:36 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.plan.premium;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
class PlanPremiumItem {

    private BigDecimal premium;

    private Set<PremiumInfluencingFactorLineItem> premiumInfluencingFactorLineItems;

    PlanPremiumItem(Set<PremiumInfluencingFactorLineItem> premiumInfluencingFactorLineItems, BigDecimal premium) {
        this.premiumInfluencingFactorLineItems = premiumInfluencingFactorLineItems;
        this.premium = premium;
    }


}
