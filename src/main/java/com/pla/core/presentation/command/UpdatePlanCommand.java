package com.pla.core.presentation.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: pradyumna
 * @since 1.0 26/03/2015
 */
@Getter
@Setter
public class UpdatePlanCommand extends CreatePlanCommand {

    private PlanId newPlanId;
    public UpdatePlanCommand() {
        super();
    }
}
