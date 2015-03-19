package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class PlanDetailConfigured implements Serializable {
    private PlanDetail planDetail;
    private PlanId planId;

    public PlanDetailConfigured(PlanId planId, PlanDetail planDetail) {
        this.planId = planId;
        this.planDetail = planDetail;
    }
}
