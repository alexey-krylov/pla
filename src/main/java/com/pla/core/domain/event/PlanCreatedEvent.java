package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

/**
 * Created by pradyumna on 11-04-2015.
 */
@Getter
public class PlanCreatedEvent {

    private final PlanId planId;


    public PlanCreatedEvent(PlanId planId) {
        this.planId = planId;
    }
}
