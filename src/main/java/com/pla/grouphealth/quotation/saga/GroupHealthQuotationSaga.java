package com.pla.grouphealth.quotation.saga;

import com.pla.grouphealth.quotation.application.command.ClosureGLQuotationCommand;
import com.pla.grouphealth.quotation.application.command.PurgeGLQuotationCommand;
import com.pla.grouphealth.quotation.domain.event.*;
import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.grouphealth.quotation.domain.model.GHQuotationStatus;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 5/31/2015.
 */
@Component
public class GroupHealthQuotationSaga extends AbstractAnnotatedSaga {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupHealthQuotationSaga.class);

    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient GHQuotationRepository ghQuotationRepository;

    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;

    private int noOfReminderSent;

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationGeneratedEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Generated Event .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(event.getQuotationId());
        LocalDate quotationGeneratedDate = groupHealthQuotation.getGeneratedOn();
        LocalDate firstReminderDate = quotationGeneratedDate.plusDays(firstReminderDay);
        LocalDate purgeDate = quotationGeneratedDate.plusDays(noOfDaysToPurge);
        LocalDate closureDate = quotationGeneratedDate.plusDays(noOfDaysToClosure);
        DateTime purgeScheduleDateTime = purgeDate.toDateTimeAtStartOfDay();
        DateTime closureScheduleDateTime = closureDate.toDateTimeAtStartOfDay();
        DateTime firstReminderDateTime = firstReminderDate.toDateTimeAtStartOfDay();
        eventScheduler.schedule(firstReminderDateTime, new GLQuotationReminderEvent(event.getQuotationId()));
        eventScheduler.schedule(purgeScheduleDateTime, new GLQuotationPurgeEvent(event.getQuotationId()));
        eventScheduler.schedule(closureScheduleDateTime, new GLQuotationClosureEvent(event.getQuotationId()));
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationPurgeEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Purge Event .....", event);
        }
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(event.getQuotationId());
        if (!GHQuotationStatus.CLOSED.equals(groupHealthQuotation.getQuotationStatus())) {
            commandGateway.send(new PurgeGLQuotationCommand(event.getQuotationId()));
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Reminder Event .....", event);
        }
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(event.getQuotationId());
        if (GHQuotationStatus.GENERATED.equals(groupHealthQuotation.getQuotationStatus())) {
            this.noOfReminderSent = noOfReminderSent + 1;
            System.out.println("************ Send Reminder ****************");
            if (this.noOfReminderSent == 1) {
                int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
                int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.QUOTATION);
                LocalDate quotationGeneratedDate = groupHealthQuotation.getGeneratedOn();
                LocalDate secondReminderDate = quotationGeneratedDate.plusDays(firstReminderDay + secondReminderDay);
                DateTime secondReminderDateTime = secondReminderDate.toDateTimeAtStartOfDay();
                eventScheduler.schedule(secondReminderDateTime, new GLQuotationReminderEvent(event.getQuotationId()));
            }
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Closure Event .....", event);
        }
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(event.getQuotationId());
        if (!GHQuotationStatus.CLOSED.equals(groupHealthQuotation.getQuotationStatus())) {
            commandGateway.send(new ClosureGLQuotationCommand(event.getQuotationId()));
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationClosedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Closure Event .....", event);
        }
    }
}
