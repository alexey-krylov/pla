/*
 * Copyright (c) 3/26/15 11:12 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.plan.premium;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
@ValueObject
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "planId")
class PlanPremium {

    private PlanId planId;

    private LocalDate effectiveFrom;

    private LocalDate validTill;

    private Set<PlanPremiumItem> planPremiumItems;


    PlanPremium(PlanId planId, Set<PlanPremiumItem> planPremiumItems, LocalDate effectiveFrom) {
        checkArgument(planId != null);
        checkArgument(effectiveFrom != null);
        checkArgument(UtilValidator.isNotEmpty(planPremiumItems));
        this.planId = planId;
        this.planPremiumItems = planPremiumItems;
        this.effectiveFrom = effectiveFrom;
    }

    public PlanPremium expire(LocalDate validTill) {
        this.validTill = validTill;
        return this;
    }
}
