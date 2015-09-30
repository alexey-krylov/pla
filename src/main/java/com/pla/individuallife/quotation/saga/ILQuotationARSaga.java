package com.pla.individuallife.quotation.saga;

import com.google.common.collect.Lists;
import com.pla.grouphealth.quotation.domain.event.GHQuotationReminderEvent;
import com.pla.grouphealth.quotation.domain.model.GHQuotationStatus;
import com.pla.individuallife.quotation.application.command.ILQuotationPurgeCommand;
import com.pla.individuallife.quotation.application.command.ILQuotationClosureCommand;
import com.pla.individuallife.quotation.application.command.ILQuotationConvertedCommand;
import com.pla.individuallife.quotation.domain.event.*;
import com.pla.individuallife.quotation.domain.model.ILQuotation;
import com.pla.individuallife.quotation.domain.model.ILQuotationStatus;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.quotation.query.ILSearchQuotationResultDto;
import com.pla.individuallife.sharedresource.event.ILQuotationConvertedToProposalEvent;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.publishedlanguage.contract.ISMEGateway;
import com.pla.sharedkernel.application.CreateQuotationNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.RolesUtil;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.repository.Repository;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Component
public class ILQuotationARSaga extends AbstractAnnotatedSaga implements Serializable {


    private transient static final Logger LOGGER = LoggerFactory.getLogger(ILQuotationARSaga.class);
    private static final long serialVersionUID = 2052253346405498007L;
    int version;
    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;
    @Autowired
    private transient EventScheduler eventScheduler;
    @Autowired
    private transient Repository<ILQuotation> ilQuotationRepository;

    @Autowired
    private transient ILQuotationFinder quotationFinder;
    @Autowired
    private transient CommandGateway commandGateway;

    private int noOfReminderSent;

    @Autowired
    private transient ISMEGateway smeGateway;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();
    //TODO discuss what happens if a quotation is just created and nothing happens after that
    //TODO Basically can it move from DRAFT to PURGE OR CLOSE OR ????
    @StartSaga
    @SagaEventHandler(associationProperty = "quotationARId")
    public void handle(ILQuotationCreatedEvent event) {
        LOGGER.debug("SAGA CREATED IL Quotation Generated Event .....", event);

    }

    @StartSaga
    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(ILQuotationSharedEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Quotation Generated Event .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);

        ILQuotation quotation = ilQuotationRepository.load(event.getQuotationId());
       /* LocalDate quotationSharedOnDate = quotation.getSharedOn();
        LocalDate firstReminderDate = quotationSharedOnDate.plusDays(firstReminderDay);
        LocalDate purgeDate = quotationSharedOnDate.plusDays(noOfDaysToPurge);
        LocalDate closureDate = quotationSharedOnDate.plusDays(noOfDaysToClosure);
        DateTime purgeScheduleDateTime = purgeDate.toDateTimeAtStartOfDay();
        DateTime closureScheduleDateTime = closureDate.toDateTimeAtStartOfDay();
        DateTime firstReminderDateTime = firstReminderDate.toDateTimeAtStartOfDay();*/

        ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(DateTime.now(), new ILQuotationReminderEvent(event.getQuotationId()));
      /*  ScheduleToken purgeScheduleToken =  eventScheduler.schedule(purgeScheduleDateTime, new ILQuotationPurgeEvent(event.getQuotationId()));
        ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new ILQuotationClosureEvent(event.getQuotationId()));*/
        scheduledTokens.add(firstReminderScheduleToken);
      /*  scheduledTokens.add(purgeScheduleToken);
        scheduledTokens.add(closureScheduleToken);*/
        List<ILQuotation> generatedVersionedQuotations = quotationFinder.findQuotationByQuotNumberAndStatusByExcludingGivenQuotId(quotation.getQuotationNumber(), quotation.getQuotationId(), GHQuotationStatus.GENERATED.name());
        if (isNotEmpty(generatedVersionedQuotations)) {
            generatedVersionedQuotations.forEach(generatedVersionedQuotation -> {
                generatedVersionedQuotation.cancelSchedules();
            });
        }
    }

    @SagaEventHandler(associationProperty = "quotationARId")
    public void handle(final ILQuotationVersionEvent event) {
        System.out.println(" Update Quotation Version Number " + event.getQuotationId());
        version++;
        Map payLoad = new HashMap();
        payLoad.put("quotationId", event.getQuotationId());
        payLoad.put("version", version);
        commandGateway.send(new GenericCommandMessage("QUOTATION_VERSION_CMD", payLoad, null));
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(ILQuotationReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Quotation Reminder Event .....", event);
        }
        ILQuotation ilQuotation = ilQuotationRepository.load(event.getQuotationId());
        if (ILQuotationStatus.SHARED.equals(ilQuotation.getIlQuotationStatus())) {
            this.noOfReminderSent = noOfReminderSent + 1;
            System.out.println("************ Send Reminder ****************");
            commandGateway.send(new CreateQuotationNotificationCommand(event.getQuotationId(), RolesUtil.INDIVIDUAL_LIFE_QUOTATION_PROCESSOR_ROLE, LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION,
                    WaitingForEnum.QUOTATION_RESPONSE, noOfReminderSent == 1 ? ReminderTypeEnum.REMINDER_1 : ReminderTypeEnum.REMINDER_2));
            if (this.noOfReminderSent == 1) {
                int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);
                int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);
                LocalDate quotationSharedDate = ilQuotation.getGeneratedOn();
                LocalDate secondReminderDate = quotationSharedDate.plusDays(firstReminderDay + secondReminderDay);
                DateTime secondReminderDateTime = secondReminderDate.toDateTimeAtStartOfDay();
                ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GHQuotationReminderEvent(event.getQuotationId()));
                scheduledTokens.add(secondReminderScheduleToken);
            }
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(ILQuotationClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Quotation Closure Event .....", event);
        }
        ILQuotation ilQuotation = ilQuotationRepository.load(event.getQuotationId());
        if (!GHQuotationStatus.CONVERTED.equals(ilQuotation.getIlQuotationStatus())) {
            commandGateway.send(new ILQuotationClosureCommand(event.getQuotationId()));
        }
        if (ilQuotation.getOpportunityId() != null) {
            smeGateway.updateOpportunityStatus(ilQuotation.getOpportunityId().getOpportunityId(), AppConstants.OPPORTUNITY_LOST_STATUS);
        }
    }


    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(ILQuotationConvertedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Quotation Closure Event .....", event);
        }
        ILQuotation ilQuotation = ilQuotationRepository.load(event.getQuotationId());
        if (ilQuotation.getOpportunityId() != null) {
            smeGateway.updateOpportunityStatus(ilQuotation.getOpportunityId().getOpportunityId(), AppConstants.OPPORTUNITY_CLOSE_STATUS);
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
     @EndSaga
     public void handle(ILQuotationPurgeEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Quotation Purge Event .....", event);
        }
        ILQuotation ilQuotation = ilQuotationRepository.load(event.getQuotationId());
        if (!GHQuotationStatus.CONVERTED.equals(ilQuotation.getIlQuotationStatus())) {
            commandGateway.send(new ILQuotationPurgeCommand(event.getQuotationId()));
        }
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(ILQuotationConvertedToProposalEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Quotation Closure Event ....", event);
        }
        List<ILSearchQuotationResultDto> quotations = quotationFinder.searchQuotation(event.getQuotationNumber(), "", "", "", "");
        List<ILSearchQuotationResultDto> quotationsExcludingCurrentOne = quotations.stream().filter( t -> !t.getQuotationId().equals(event.getQuotationId())).collect(Collectors.toList());
        commandGateway.send(new ILQuotationConvertedCommand(event.getQuotationId()));
        quotationsExcludingCurrentOne.forEach(quotation -> {
            commandGateway.send(new ILQuotationClosureCommand(new QuotationId(quotation.getQuotationId())));
        });
    }

    @SagaEventHandler(associationProperty = "quotationId")
    @EndSaga
    public void handle(ILQuotationEndSagaEvent event) {
        scheduledTokens.forEach(scheduledToken -> {
            eventScheduler.cancelSchedule(scheduledToken);
        });
    }
}
