/*
 * Copyright (c) 3/26/15 11:13 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.plan.premium;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.identifier.CoverageId;
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
@EqualsAndHashCode(of = {"coverageId", "planId"})
class CoveragePremium {

    private CoverageId coverageId;

    private PlanId planId;

    private LocalDate effectiveFrom;

    private LocalDate validTill;

    private Set<CoveragePremiumItem> coveragePremiumItems = Sets.newHashSet();

    CoveragePremium(PlanId planId, CoverageId coverageId, Set<CoveragePremiumItem> coveragePremiumItems) {
        checkArgument(coverageId != null);
        checkArgument(planId != null);
        checkArgument(UtilValidator.isNotEmpty(coveragePremiumItems));
        this.coverageId = coverageId;
        this.planId = planId;
        this.coveragePremiumItems = coveragePremiumItems;
    }

    public CoveragePremium addCoveragePremiumItem(CoveragePremiumItem coveragePremiumItem) {
        this.coveragePremiumItems.add(coveragePremiumItem);
        return this;
    }
}
