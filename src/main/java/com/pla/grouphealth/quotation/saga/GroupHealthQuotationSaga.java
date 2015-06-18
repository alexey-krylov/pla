package com.pla.grouphealth.quotation.saga;

import com.google.common.collect.Lists;
import com.pla.grouphealth.quotation.application.command.GHClosureGLQuotationCommand;
import com.pla.grouphealth.quotation.application.command.GHPurgeGLQuotationCommand;
import com.pla.grouphealth.quotation.domain.event.*;
import com.pla.grouphealth.quotation.domain.model.GHQuotationStatus;
import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
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

import java.util.List;

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

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @StartSaga
    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GHQuotationGeneratedEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Quotation Generated Event .....", event);
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
        ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new GHQuotationReminderEvent(event.getQuotationId()));
        ScheduleToken purgeScheduleToken = eventScheduler.schedule(purgeScheduleDateTime, new GHQuotationPurgeEvent(event.getQuotationId()));
        ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new GHQuotationClosureEvent(event.getQuotationId()));
        scheduledTokens.add(firstReminderScheduleToken);
        scheduledTokens.add(purgeScheduleToken);
        scheduledTokens.add(closureScheduleToken);
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GHQuotationPurgeEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Quotation Purge Event .....", event);
        }
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(event.getQuotationId());
        if (!GHQuotationStatus.CLOSED.equals(groupHealthQuotation.getQuotationStatus())) {
            commandGateway.send(new GHPurgeGLQuotationCommand(event.getQuotationId()));
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GHQuotationReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Quotation Reminder Event .....", event);
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
                ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GHQuotationReminderEvent(event.getQuotationId()));
                scheduledTokens.add(secondReminderScheduleToken);
            }
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GHQuotationClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Quotation Closure Event .....", event);
        }
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(event.getQuotationId());
        if (!GHQuotationStatus.CLOSED.equals(groupHealthQuotation.getQuotationStatus())) {
            commandGateway.send(new GHClosureGLQuotationCommand(event.getQuotationId()));
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GHQuotationClosedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Quotation Closure Event .....", event);
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GHQuotationEndSagaEvent event) {
        scheduledTokens.forEach(scheduledToken -> {
            eventScheduler.cancelSchedule(scheduledToken);
        });
    }
}
