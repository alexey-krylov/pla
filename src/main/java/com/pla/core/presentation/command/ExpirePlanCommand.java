package com.pla.core.presentation.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 5/30/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpirePlanCommand {

    private PlanId planId;
}
