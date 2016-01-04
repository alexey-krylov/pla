package com.pla.core.presentation.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Admin on 05-Jan-16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkPlanLaunchCommand {

    private Set<PlanId> planId;

}
