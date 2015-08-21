package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

/**
 * Created by pradyumna on 12-04-2015.
 */
@Getter
public class PlanDeletedEvent {

    private PlanId planId;

    public PlanDeletedEvent(PlanId planId) {
        this.planId = planId;
    }
}
