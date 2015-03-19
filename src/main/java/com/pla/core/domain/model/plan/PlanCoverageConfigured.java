package com.pla.core.domain.model.plan;

import lombok.Getter;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class PlanCoverageConfigured {
    private Set<PlanCoverage> coverages;

    public PlanCoverageConfigured(Set<PlanCoverage> coverages) {
        this.coverages = coverages;
    }
}
