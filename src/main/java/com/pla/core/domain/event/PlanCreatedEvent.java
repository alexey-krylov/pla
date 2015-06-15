package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by pradyumna on 11-04-2015.
 */
@Getter
public class PlanCreatedEvent {

    private final PlanId planId;
    private DateTime launchDate;

    public PlanCreatedEvent(PlanId planId, DateTime launchDate) {
        this.planId = planId;
        this.launchDate = launchDate;
    }

}
