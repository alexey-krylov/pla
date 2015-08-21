package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * Created by Samir on 5/30/2015.
 */
@Getter
@ToString
public class PlanWithdrawnEvent {

    private PlanId planId;

    private DateTime withDrawlDate;

    public PlanWithdrawnEvent(PlanId planId, DateTime withDrawlDate) {
        this.planId = planId;
        this.withDrawlDate = withDrawlDate;
    }

}
