package com.pla.core.domain.model.plan;

import lombok.Getter;

/**
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
