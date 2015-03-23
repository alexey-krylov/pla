package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 22/03/2015
 */
@Getter
public class PlanCoverageRegularTermConfigured {
    private Set<Integer> validTerms;
    private int maxMaturityAge;
    private CoverageId coverageId;
    private PlanId planId;
    private CoverageTermType coverageTermType;

    public PlanCoverageRegularTermConfigured(PlanId planId, CoverageId coverageId, CoverageTermType coverageTermType, Set<Integer> validTerms, int maxMaturityAge) {
        this.planId = planId;
        this.coverageId = coverageId;
        this.validTerms = validTerms;
        this.maxMaturityAge = maxMaturityAge;
        this.coverageTermType = coverageTermType;
    }
}
