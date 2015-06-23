package com.pla.grouplife.quotation.saga;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.contract.ISMEGateway;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.grouplife.quotation.application.command.ClosureGLQuotationCommand;
import com.pla.grouplife.quotation.application.command.PurgeGLQuotationCommand;
import com.pla.grouplife.quotation.domain.event.*;
import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.quotation.domain.model.QuotationStatus;
import com.pla.grouplife.quotation.repository.GlQuotationRepository;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
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
import org.nthdimenzion.common.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 5/31/2015.
 */
@Component
public class GroupLifeQuotationSaga extends AbstractAnnotatedSaga {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupLifeQuotationSaga.class);

    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient GlQuotationRepository glQuotationRepository;

    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;

    private int noOfReminderSent;

    @Autowired
    private transient CommandGateway commandGateway;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @Autowired
    private transient ISMEGateway smeGateway;


    @StartSaga
    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationGeneratedEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Generated Event .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        LocalDate quotationGeneratedDate = groupLifeQuotation.getGeneratedOn();
        LocalDate firstReminderDate = quotationGeneratedDate.plusDays(firstReminderDay);
        LocalDate purgeDate = quotationGeneratedDate.plusDays(noOfDaysToPurge);
        LocalDate closureDate = quotationGeneratedDate.plusDays(noOfDaysToClosure);
        DateTime purgeScheduleDateTime = purgeDate.toDateTimeAtStartOfDay();
        DateTime closureScheduleDateTime = closureDate.toDateTimeAtStartOfDay();
        DateTime firstReminderDateTime = firstReminderDate.toDateTimeAtStartOfDay();
        ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new GLQuotationReminderEvent(event.getQuotationId()));
        ScheduleToken purgeScheduleToken = eventScheduler.schedule(purgeScheduleDateTime, new GLQuotationPurgeEvent(event.getQuotationId()));
        ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new GLQuotationClosureEvent(event.getQuotationId()));
        scheduledTokens.add(firstReminderScheduleToken);
        scheduledTokens.add(purgeScheduleToken);
        scheduledTokens.add(closureScheduleToken);
        List<GroupLifeQuotation> generatedVersionedQuotations = glQuotationRepository.findQuotationByQuotNumberAndStatusByExcludingGivenQuotId(groupLifeQuotation.getQuotationNumber(), groupLifeQuotation.getQuotationId(), QuotationStatus.GENERATED.name());
        if (isNotEmpty(generatedVersionedQuotations)) {
            generatedVersionedQuotations.forEach(generatedVersionedQuotation -> {
                generatedVersionedQuotation.cancelSchedules();
            });
        }
        smeGateway.updateOpportunityStatus(groupLifeQuotation.getOpportunityId().getOpportunityId(), AppConstants.OPPORTUNITY_CLOSE_STATUS);
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationPurgeEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Purge Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (!QuotationStatus.CLOSED.equals(groupLifeQuotation.getQuotationStatus())) {
            commandGateway.send(new PurgeGLQuotationCommand(event.getQuotationId()));
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Reminder Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (QuotationStatus.GENERATED.equals(groupLifeQuotation.getQuotationStatus())) {
            this.noOfReminderSent = noOfReminderSent + 1;
            System.out.println("************ Send Reminder ****************");
            if (this.noOfReminderSent == 1) {
                int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
                int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
                LocalDate quotationGeneratedDate = groupLifeQuotation.getGeneratedOn();
                LocalDate secondReminderDate = quotationGeneratedDate.plusDays(firstReminderDay + secondReminderDay);
                DateTime secondReminderDateTime = secondReminderDate.toDateTimeAtStartOfDay();
                ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GLQuotationReminderEvent(event.getQuotationId()));
                scheduledTokens.add(secondReminderScheduleToken);
            }
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Closure Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (!QuotationStatus.CLOSED.equals(groupLifeQuotation.getQuotationStatus())) {
            commandGateway.send(new ClosureGLQuotationCommand(event.getQuotationId()));
        }
        if (isNotEmpty(groupLifeQuotation.getOpportunityId().getOpportunityId())) {
            smeGateway.updateOpportunityStatus(groupLifeQuotation.getOpportunityId().getOpportunityId(), "");
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationClosedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Closure Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (isNotEmpty(groupLifeQuotation.getOpportunityId().getOpportunityId())) {
            smeGateway.updateOpportunityStatus(groupLifeQuotation.getOpportunityId().getOpportunityId(), "");
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationEndSagaEvent event) {
        scheduledTokens.forEach(scheduledToken -> {
            eventScheduler.cancelSchedule(scheduledToken);
        });
    }
}
