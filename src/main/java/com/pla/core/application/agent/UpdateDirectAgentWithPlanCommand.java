package com.pla.core.application.agent;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 14-Dec-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateDirectAgentWithPlanCommand {

    private PlanId planId;

    private String agentId;

    public UpdateDirectAgentWithPlanCommand(PlanId planId,String agentId){
        this.planId = planId;
        this.agentId = agentId;
    }
}
