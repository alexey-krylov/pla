package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.PlanId;

/**
 * @author: pradyumna
 * @since 1.0 19/03/2015
 */
public class PlanDetailChanged extends PlanDetailConfigured {
    public PlanDetailChanged(PlanId planId, PlanDetail planDetail) {
        super(planId, planDetail);
    }
}
