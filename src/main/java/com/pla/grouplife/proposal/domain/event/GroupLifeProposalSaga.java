package com.pla.grouplife.proposal.domain.event;

import com.google.common.collect.Lists;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.sharedresource.model.vo.GLProposalStatus;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.application.CreateProposalNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.service.GLMandatoryDocumentChecker;
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
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Samir on 7/14/2015.
 */
@Component
public class GroupLifeProposalSaga extends AbstractAnnotatedSaga implements Serializable {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupLifeProposalSaga.class);

    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private Repository<GroupLifeProposal> groupLifeProposalRepository;

    @Autowired
    private GLMandatoryDocumentChecker glMandatoryDocumentChecker;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @StartSaga
    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GLProposalSubmitEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Proposal Submitted Event .....", event);
        }
        if (glMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())) {
            int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL);
            int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL);
            int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL);
            GroupLifeProposal proposalAggregate = groupLifeProposalRepository.load(event.getProposalId());
            DateTime proposalSubmitDate = proposalAggregate.getSubmittedOn();
            DateTime firstReminderDateTime = proposalSubmitDate.plusDays(firstReminderDay);
            DateTime purgeScheduleDateTime = proposalSubmitDate.plusDays(noOfDaysToPurge);
            DateTime closureScheduleDateTime = proposalSubmitDate.plusDays(noOfDaysToClosure);
            ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new GLProposalFirstReminderEvent(event.getProposalId()));
            ScheduleToken purgeScheduleToken = eventScheduler.schedule(purgeScheduleDateTime, new GLProposalPurgeEvent(event.getProposalId()));
            ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new GLProposalClosureEvent(event.getProposalId()));
            scheduledTokens.add(firstReminderScheduleToken);
            scheduledTokens.add(purgeScheduleToken);
            scheduledTokens.add(closureScheduleToken);
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GLProposalFirstReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Proposal Reminder Event .....", event);
        }
        GroupLifeProposal proposalAggregate = groupLifeProposalRepository.load(event.getProposalId());
        if (GLProposalStatus.PENDING_ACCEPTANCE.equals(proposalAggregate.getProposalStatus()) && glMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())) {
            commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.GROUP_LIFE_PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL,
                    WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_1));
                int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL);
                int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL);
                DateTime proposalSharedDate = proposalAggregate.getSubmittedOn();
                DateTime secondReminderDateTime = proposalSharedDate.plusDays(firstReminderDay + secondReminderDay);
                ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GLProposalSecondReminderEvent(event.getProposalId()));
                scheduledTokens.add(secondReminderScheduleToken);
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GLProposalSecondReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Proposal Reminder Event .....", event);
        }
        GroupLifeProposal proposalAggregate = groupLifeProposalRepository.load(event.getProposalId());
        if (GLProposalStatus.PENDING_ACCEPTANCE.equals(proposalAggregate.getProposalStatus()) && glMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())) {
            commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.GROUP_LIFE_PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL,
                    WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_2));
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GLProposalClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Proposal Closure Event .....", event);
        }
        if (glMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())){
            commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.GROUP_LIFE_PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_LIFE, ProcessType.PROPOSAL,
                    WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.CANCELLATION));
        }
    }




}
