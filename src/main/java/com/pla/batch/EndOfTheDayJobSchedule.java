package com.pla.batch;

import com.pla.core.presentation.command.MarkPlanLaunchCommand;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 05-Jan-16.
 */
@Component
public class EndOfTheDayJobSchedule{

    private static final Logger LOGGER = LoggerFactory.getLogger(EndOfTheDayJobSchedule.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private PlanFinder planFinder;

    @Scheduled(cron = "0 1 1 * * ?")
    public void markPlanLaunchSchedule() {
        System.out.println("The time is now " + dateFormat.format(new Date()));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start the Plan Launch Job.....");
        }
        Set<PlanId> planByLaunchDate = planFinder.getPlanByLaunchDate();
        if (isNotEmpty(planByLaunchDate)){
            commandGateway.sendAndWait(new MarkPlanLaunchCommand(planByLaunchDate));
        }
        planFinder.markPlanLaunched();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End the Plan Launch Job.....");
        }
    }
}
