package com.pla.core.saga;

import com.google.common.collect.Lists;
import com.pla.core.domain.event.*;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.presentation.command.ExpirePlanCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Samir on 5/30/2015.
 */
@Component
public class PlanSaga extends AbstractAnnotatedSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient GenericMongoRepository<Plan> planMongoRepository;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @StartSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanCreatedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Withdrawn Event .....", event);
        }
        DateTime launchDate = event.getLaunchDate();
        ScheduleToken scheduleToken = eventScheduler.schedule(launchDate, new PlanLaunchEvent(event.getPlanId()));
        scheduledTokens.add(scheduleToken);
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanUpdatedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan updating Event .....", event);
        }
        DateTime launchDate = event.getLaunchDate();
        ScheduleToken scheduleToken = eventScheduler.schedule(launchDate, new PlanLaunchEvent(event.getPlanId()));
        scheduledTokens.add(scheduleToken);
    }

    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanLaunchEvent event) {
        System.out.println(" Plan Launch Event ********");
        Plan plan = planMongoRepository.load(event.getPlanId());
        plan.markLaunched();
        end();
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanWithdrawnEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Withdrawn Event .....", event);
        }
        DateTime withDrawnDate = event.getWithDrawlDate();
        ScheduleToken scheduleToken = eventScheduler.schedule(withDrawnDate, new PlanExpireEvent(event.getPlanId()));
        scheduledTokens.add(scheduleToken);
    }

    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanExpireEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Expire Event .....", event);
        }
        commandGateway.send(new ExpirePlanCommand(event.getPlanId()));
        end();
    }
}
