package com.pla.core.presentation.command;

import com.pla.core.domain.exception.DuplicatePlanException;
import com.pla.core.domain.exception.PlanException;

import java.util.concurrent.TimeoutException;

/**
 * Created by pradyumna on 21-04-2015.
 */
public interface PlanCommandGateway {

    void createPlan(CreatePlanCommand command)
            throws PlanException;

    void updatePlan(CreatePlanCommand command)
            throws PlanException;
}
