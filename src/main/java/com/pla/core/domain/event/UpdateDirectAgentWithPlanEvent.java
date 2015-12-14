package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Admin on 14-Dec-15.
 */
@Getter
@ToString
public class UpdateDirectAgentWithPlanEvent {

    private final PlanId planId;

    private String agentId;

    public UpdateDirectAgentWithPlanEvent(PlanId planId,String agentId){
        this.planId = planId;
        this.agentId = agentId;
    }
}
