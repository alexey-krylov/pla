package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.PolicyTermType;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.util.Set;

/**
 * The PolicyTermConfigured Event references the Policy Term VO as it is
 * only used internal.
 * <p/>
 * In case the events needs to be propagated outside the domain this has
 * to be change to contain a DTO rather than a VO.
 *
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class PolicyTermConfigured {
    /**
     * The valid terms will hold eligible terms with respect to ages
     * of policy holder or policy years.
     */
    Set<Integer> validTerms;
    int maxMaturityAge;
    private PolicyTermType policyTermType;
    private PlanId planId;

    public PolicyTermConfigured(PlanId planId, PolicyTermType policyTermType, Set<Integer> validTerms, int maxMaturityAge) {
        this.planId = planId;
        this.policyTermType = policyTermType;
        this.validTerms = validTerms;
        this.maxMaturityAge = maxMaturityAge;
    }
}
