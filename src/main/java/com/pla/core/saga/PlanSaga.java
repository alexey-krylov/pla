package com.pla.core.saga;

import com.pla.core.domain.event.PlanCreatedEvent;
import com.pla.core.domain.event.PlanExpireEvent;
import com.pla.core.domain.event.PlanLaunchEvent;
import com.pla.core.domain.event.PlanWithdrawnEvent;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.presentation.command.ExpirePlanCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @StartSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanWithdrawnEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Withdrawn Event .....", event);
        }
        LocalDate withDrawnDate = event.getWithDrawlDate();
        DateTime scheduleDateTime = withDrawnDate.toDateTimeAtStartOfDay();
        eventScheduler.schedule(scheduleDateTime, new PlanExpireEvent(event.getPlanId()));
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanCreatedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Withdrawn Event .....", event);
        }
        LocalDate launchDate = event.getLaunchDate();
        DateTime scheduleDateTime = launchDate.toDateTimeAtStartOfDay();
        eventScheduler.schedule(scheduleDateTime, new PlanLaunchEvent(event.getPlanId()));
    }

    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanLaunchEvent event) {
        System.out.println(" Plan Launch Event ********");
        Plan plan = planMongoRepository.load(event.getPlanId());
        plan.markLaunched();
        end();
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
