package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
@Getter
public class PlanCoverageTermConfigured {
    private CoverageId coverageId;
    private Set<Integer> validTerms;
    private int maxMaturityAge;
    private CoverageTermType coverageTermType;
    private PlanId planId;

    public PlanCoverageTermConfigured(PlanId planId, CoverageId coverageId, CoverageTermType coverageTermType,
                                      Set<Integer> validTerms, int maxMaturityAge) {
        this.planId = planId;
        this.coverageId = coverageId;
        this.coverageTermType = coverageTermType;
        this.validTerms = validTerms;
        this.maxMaturityAge = maxMaturityAge;
    }
}
