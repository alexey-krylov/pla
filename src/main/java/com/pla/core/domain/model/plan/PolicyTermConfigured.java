package com.pla.core.domain.model.plan;

import lombok.Getter;

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
    private PolicyTerm policyTerm;

    public PolicyTermConfigured(PolicyTerm policyTerm) {
        this.policyTerm = policyTerm;
    }
}
