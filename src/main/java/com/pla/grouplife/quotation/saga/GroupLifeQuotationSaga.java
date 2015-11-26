package com.pla.grouplife.quotation.saga;

import com.google.common.collect.Lists;
import com.pla.grouplife.quotation.application.command.GLQuotationClosureCommand;
import com.pla.grouplife.quotation.application.command.GLQuotationConvertedCommand;
import com.pla.grouplife.quotation.application.command.PurgeGLQuotationCommand;
import com.pla.grouplife.quotation.domain.event.*;
import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.quotation.domain.model.QuotationStatus;
import com.pla.grouplife.quotation.repository.GlQuotationRepository;
import com.pla.grouplife.sharedresource.event.GLQuotationConvertedToProposalEvent;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.publishedlanguage.contract.ISMEGateway;
import com.pla.sharedkernel.application.CreateQuotationNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.util.RolesUtil;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    @Autowired
    private transient CommandGateway commandGateway;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @Autowired
    private transient ISMEGateway smeGateway;


    @StartSaga
    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationSharedEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Generated Event .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        LocalDate quotationSharedDate = groupLifeQuotation.getSharedOn();
        LocalDate firstReminderDate = quotationSharedDate.plusDays(firstReminderDay);
        LocalDate purgeDate = quotationSharedDate.plusDays(noOfDaysToPurge);
        LocalDate closureDate = quotationSharedDate.plusDays(noOfDaysToClosure);
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
        if (QuotationStatus.SHARED.equals(groupLifeQuotation.getQuotationStatus())) {
            commandGateway.send(new CreateQuotationNotificationCommand(event.getQuotationId(), RolesUtil.GROUP_LIFE_QUOTATION_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION,
                    WaitingForEnum.QUOTATION_RESPONSE, ReminderTypeEnum.REMINDER_1));
            int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
            int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION);
            LocalDate quotationSharedDate = groupLifeQuotation.getSharedOn();
            LocalDate secondReminderDate = quotationSharedDate.plusDays(firstReminderDay + secondReminderDay);
            DateTime secondReminderDateTime = secondReminderDate.toDateTimeAtStartOfDay();
            ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GLQuotationSecondReminderEvent(event.getQuotationId()));
            scheduledTokens.add(secondReminderScheduleToken);
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationSecondReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Reminder Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (QuotationStatus.SHARED.equals(groupLifeQuotation.getQuotationStatus())) {
            commandGateway.send(new CreateQuotationNotificationCommand(event.getQuotationId(), RolesUtil.GROUP_LIFE_QUOTATION_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.QUOTATION,
                    WaitingForEnum.QUOTATION_RESPONSE, ReminderTypeEnum.REMINDER_2));
        }
    }


    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(GLQuotationClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Closure Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (!QuotationStatus.CLOSED.equals(groupLifeQuotation.getQuotationStatus())) {
            commandGateway.send(new GLQuotationClosureCommand(event.getQuotationId()));
        }
        if (groupLifeQuotation.getOpportunityId() != null) {
            smeGateway.updateOpportunityStatus(groupLifeQuotation.getOpportunityId().getOpportunityId(), AppConstants.OPPORTUNITY_LOST_STATUS);
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationConvertedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Closure Event .....", event);
        }
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(event.getQuotationId());
        if (groupLifeQuotation.getOpportunityId() != null) {
            smeGateway.updateOpportunityStatus(groupLifeQuotation.getOpportunityId().getOpportunityId(), AppConstants.OPPORTUNITY_CLOSE_STATUS);
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationEndSagaEvent event) {
        scheduledTokens.forEach(scheduledToken -> {
            eventScheduler.cancelSchedule(scheduledToken);
        });
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(GLQuotationConvertedToProposalEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Quotation Closure Event .....", event);
        }
        List<GroupLifeQuotation> groupLifeQuotations = glQuotationRepository.findQuotationByQuotationNumber(event.getQuotationNumber());
        List<GroupLifeQuotation> quotationsExcludingCurrentOne = groupLifeQuotations.stream().filter(new Predicate<GroupLifeQuotation>() {
            @Override
            public boolean test(GroupLifeQuotation groupLifeQuotation) {
                return !event.getQuotationId().equals(groupLifeQuotation.getIdentifier());
            }
        }).collect(Collectors.toList());

        commandGateway.send(new GLQuotationConvertedCommand(event.getQuotationId()));
        quotationsExcludingCurrentOne.forEach(groupHealthQuotation -> {
            commandGateway.send(new GLQuotationClosureCommand(groupHealthQuotation.getQuotationId()));
        });

    }
}
