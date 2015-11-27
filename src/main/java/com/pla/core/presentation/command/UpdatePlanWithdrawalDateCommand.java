package com.pla.core.presentation.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Admin on 27-Nov-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanWithdrawalDateCommand {

    private PlanId planId;

    private DateTime withdrawalDate;

}
