package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Author - Mohan Sharma Created on 11-04-2015.
 */
@Getter
public class PlanUpdatedEvent {

    private final PlanId planId;
    private DateTime launchDate;

    public PlanUpdatedEvent(PlanId planId, DateTime launchDate) {
        this.planId = planId;
        this.launchDate = launchDate;
    }

}
