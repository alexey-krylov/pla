    package com.pla.grouplife.claim.domain.event;

import com.google.common.collect.Lists;
import com.pla.sharedkernel.application.CreateClaimNotificationCommand;
import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.domain.model.GroupLifeClaim;
import com.pla.sharedkernel.service.GLClaimMandatoryDocumentChecker;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.util.RolesUtil;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.repository.Repository;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by ak
 */

public class GroupLifeClaimSaga extends AbstractAnnotatedSaga {
    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupLifeClaimSaga.class);
    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private Repository<GroupLifeClaim> groupLifeClaimRepository;

    @Autowired
    private GLClaimMandatoryDocumentChecker glClaimMandatoryDocumentChecker;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();


    @StartSaga
    @SagaEventHandler(associationProperty = "claimId")
    public void handle(GLClaimSubmitEvent event) throws ProcessInfoException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Claim Submitted Event .....", event);
        }

        if (glClaimMandatoryDocumentChecker.isRequiredForSubmission(event.getClaimId().getClaimId())) {
            int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM);
            int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM);
            int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM);
            GroupLifeClaim claimAggregate = groupLifeClaimRepository.load(event.getClaimId());
            DateTime claimSubmitDate = claimAggregate.getSubmittedOn();
            DateTime firstReminderDateTime = claimSubmitDate.plusDays(firstReminderDay);
            DateTime purgeScheduleDateTime = claimSubmitDate.plusDays(noOfDaysToPurge);
            DateTime closureScheduleDateTime = claimSubmitDate.plusDays(noOfDaysToClosure);
            ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new GLClaimFirstReminderEvent(event.getClaimId()));
            ScheduleToken purgeScheduleToken = eventScheduler.schedule(purgeScheduleDateTime, new GLClaimPurgeEvent(event.getClaimId()));
            ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new GLClaimClosureEvent(event.getClaimId()));
            scheduledTokens.add(firstReminderScheduleToken);
            scheduledTokens.add(purgeScheduleToken);
            scheduledTokens.add(closureScheduleToken);
        }
    }

    @SagaEventHandler(associationProperty = "claimId")
    public void handle(GLClaimFirstReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Claim Reminder Event .....", event);
        }
        GroupLifeClaim claimAggregate = groupLifeClaimRepository.load(event.getClaimId());
        if (ClaimStatus.EVALUATION.equals(claimAggregate.getClaimStatus()) && glClaimMandatoryDocumentChecker.isRequiredForSubmission(event.getClaimId().getClaimId())) {
            commandGateway.send(new CreateClaimNotificationCommand(event.getClaimId(), RolesUtil.GROUP_LIFE_CLAIM_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM,
                    WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_1));
            int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM);
            int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM);
            DateTime proposalSharedDate = claimAggregate.getSubmittedOn();
            DateTime secondReminderDateTime = proposalSharedDate.plusDays(firstReminderDay + secondReminderDay);
            ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GLClaimSecondReminderEvent(event.getClaimId()));
            scheduledTokens.add(secondReminderScheduleToken);
        }


    }

    @SagaEventHandler(associationProperty = "claimId")
    public void handle(GLClaimSecondReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Claim Reminder Event .....", event);
        }
        GroupLifeClaim claimAggregate = groupLifeClaimRepository.load(event.getClaimId());
        if (ClaimStatus.EVALUATION.equals(claimAggregate.getClaimStatus()) && glClaimMandatoryDocumentChecker.isRequiredForSubmission(event.getClaimId().getClaimId())) {
            commandGateway.send(new CreateClaimNotificationCommand(event.getClaimId(), RolesUtil.GROUP_LIFE_CLAIM_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM,
                    WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_2));
        }
    }

    @SagaEventHandler(associationProperty = "claimId")
    public void handle(GLClaimClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Claim Closure Event .....", event);
        }
        if (glClaimMandatoryDocumentChecker.isRequiredForSubmission(event.getClaimId().getClaimId())) {
            commandGateway.send(new CreateClaimNotificationCommand(event.getClaimId(), RolesUtil.GROUP_LIFE_CLAIM_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.CLAIM,
                    WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.CANCELLATION));
        }
    }
}