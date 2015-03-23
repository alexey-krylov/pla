package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
@Getter
public class PremiumTermConfigured {
    private PremiumTermType termType;
    private Set<Integer> validTerms;
    private int premiumCutOffAge;
    private PlanId planId;

    public PremiumTermConfigured(PlanId planId, PremiumTermType termType, Set<Integer> validTerms, int premiumCutOffAge) {
        this.planId = planId;
        this.termType = termType;
        this.validTerms = validTerms;
        this.premiumCutOffAge = premiumCutOffAge;
    }
}
