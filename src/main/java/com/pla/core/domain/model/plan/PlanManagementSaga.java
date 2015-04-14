package com.pla.core.domain.model.plan;

import com.pla.core.application.service.plan.premium.PremiumService;
import com.pla.core.domain.event.PlanCreatedEvent;
import com.pla.core.domain.event.PlanDeletedEvent;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by pradyumna on 11-04-2015.
 */
@Component
public class PlanManagementSaga extends AbstractAnnotatedSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanManagementSaga.class);
    private boolean planPremiumAvailable = false;
    private PremiumService premiumService;

    @StartSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanCreatedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Deleted Event .....");
        }
    }


    @EndSaga
    @SagaEventHandler(associationProperty = "planId")
    public void handle(PlanDeletedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Plan Deleted Event .....");
        }
        super.end();
    }


}
