package com.pla.core.domain.model.plan;

import lombok.Getter;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
@Getter
public class CoverageBenefitConfigured {
    private Set<PlanCoverageBenefit> benefits;

    public CoverageBenefitConfigured(Set<PlanCoverageBenefit> benefits) {
        this.benefits = benefits;
    }
}
