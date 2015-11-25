package com.pla.grouphealth.proposal.domain.event;

import com.google.common.collect.Lists;
import com.pla.grouphealth.proposal.application.command.GHProposalClosureCommand;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.application.CreateProposalNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.service.GHMandatoryDocumentChecker;
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
 * Created by Samir on 7/6/2015.
 */
@Component
public class GroupHealthProposalSaga extends AbstractAnnotatedSaga implements Serializable {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupHealthProposalSaga.class);


    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private GHMandatoryDocumentChecker ghMandatoryDocumentChecker;

    @Autowired
    private Repository<GroupHealthProposal> ghProposalMongoRepository;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @StartSaga
    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GHProposalSubmitEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Proposal Submitted Event .....", event);
        }
        if (ghMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())) {
            int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
            int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
            int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
            GroupHealthProposal proposalAggregate = ghProposalMongoRepository.load(event.getProposalId());
            DateTime proposalSubmitDate = proposalAggregate.getSubmittedOn();
            DateTime firstReminderDateTime = proposalSubmitDate.plusDays(firstReminderDay);
            DateTime purgeScheduleDateTime = proposalSubmitDate.plusDays(noOfDaysToPurge);
            DateTime closureScheduleDateTime = proposalSubmitDate.plusDays(noOfDaysToClosure);
            ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new GHProposalFirstReminderEvent(event.getProposalId()));
            ScheduleToken purgeScheduleToken = eventScheduler.schedule(purgeScheduleDateTime, new GHProposalPurgeEvent(event.getProposalId()));
            ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new GHProposalClosureEvent(event.getProposalId()));
            scheduledTokens.add(firstReminderScheduleToken);
            scheduledTokens.add(purgeScheduleToken);
            scheduledTokens.add(closureScheduleToken);
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GHProposalFirstReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Proposal Reminder Event .....", event);
        }
        GroupHealthProposal proposalAggregate = ghProposalMongoRepository.load(event.getProposalId());
        if (ProposalStatus.PENDING_ACCEPTANCE.equals(proposalAggregate.getProposalStatus()) && ghMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())) {
            commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.GROUP_HEALTH_PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL,
                    WaitingForEnum.MANDATORY_DOCUMENTS,ReminderTypeEnum.REMINDER_1));
                int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
                int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL);
                DateTime proposalSharedDate = proposalAggregate.getSubmittedOn();
                DateTime secondReminderDateTime = proposalSharedDate.plusDays(firstReminderDay + secondReminderDay);
                ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GHProposalSecondReminderEvent(event.getProposalId()));
                scheduledTokens.add(secondReminderScheduleToken);
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GHProposalSecondReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Proposal Second Reminder Event .....", event);
        }
        GroupHealthProposal proposalAggregate = ghProposalMongoRepository.load(event.getProposalId());
        if (ProposalStatus.PENDING_ACCEPTANCE.equals(proposalAggregate.getProposalStatus()) && ghMandatoryDocumentChecker.isRequiredForSubmission(event.getProposalId().getProposalId())) {
            commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.GROUP_HEALTH_PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL,
                    WaitingForEnum.MANDATORY_DOCUMENTS,ReminderTypeEnum.REMINDER_2));
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(GHProposalClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GH Proposal Closure Event .....", event);
        }
        GroupHealthProposal proposalAggregate = ghProposalMongoRepository.load(event.getProposalId());
        commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.GROUP_HEALTH_PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.PROPOSAL,
                WaitingForEnum.MANDATORY_DOCUMENTS,ReminderTypeEnum.CANCELLATION ));
        if (ProposalStatus.PENDING_ACCEPTANCE.equals(proposalAggregate.getProposalStatus())) {
            commandGateway.send(new GHProposalClosureCommand(event.getProposalId()));
        }
    }
}
