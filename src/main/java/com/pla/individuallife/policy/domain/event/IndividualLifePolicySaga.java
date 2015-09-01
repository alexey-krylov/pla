package com.pla.individuallife.policy.domain.event;

import com.google.common.collect.Lists;
import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.policy.domain.model.PolicyStatus;
import com.pla.individuallife.proposal.application.command.ILProposalClosureCommand;
import com.pla.individuallife.proposal.domain.event.ILProposalClosureEvent;
import com.pla.individuallife.proposal.domain.event.ILProposalPurgeEvent;
import com.pla.individuallife.proposal.domain.event.ILProposalReminderEvent;
import com.pla.individuallife.proposal.domain.event.ILProposalSubmitEvent;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.application.CreateProposalNotificationCommand;
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
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 8/31/2015.
 */
@Component
public class IndividualLifePolicySaga extends AbstractAnnotatedSaga implements Serializable {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(ILPolicyEventHandler.class);

    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;

    private int noOfReminderSent;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private Repository<IndividualLifePolicy> ilPolicyMongoRepository;

    private List<ScheduleToken> scheduledTokens = Lists.newArrayList();

    @StartSaga
    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(ILProposalSubmitEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Proposal Submitted Event .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL);
        IndividualLifePolicy proposalAggregate = ilPolicyMongoRepository.load(event.getProposalId());
        DateTime proposalSubmitDate = proposalAggregate.getInceptionOn();
        DateTime firstReminderDateTime = proposalSubmitDate.plusDays(firstReminderDay);
        DateTime purgeScheduleDateTime = proposalSubmitDate.plusDays(noOfDaysToPurge);
        DateTime closureScheduleDateTime = proposalSubmitDate.plusDays(noOfDaysToClosure);
        ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new ILProposalReminderEvent(event.getProposalId()));
        ScheduleToken purgeScheduleToken = eventScheduler.schedule(purgeScheduleDateTime, new ILProposalPurgeEvent(event.getProposalId()));
        ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new ILProposalClosureEvent(event.getProposalId()));
        scheduledTokens.add(firstReminderScheduleToken);
        scheduledTokens.add(purgeScheduleToken);
        scheduledTokens.add(closureScheduleToken);
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(ILProposalReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Proposal Reminder Event .....", event);
        }
        IndividualLifePolicy proposalAggregate = ilPolicyMongoRepository.load(event.getProposalId());
        if (PolicyStatus.IN_FORCE.equals(proposalAggregate.getPolicyStatus())) {
            this.noOfReminderSent = noOfReminderSent + 1;
            System.out.println("************ Send Reminder ****************");
            commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.INDIVIDUAL_LIFE__PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL,
                    WaitingForEnum.MANDATORY_DOCUMENTS, noOfReminderSent == 1 ? ReminderTypeEnum.REMINDER_1 : ReminderTypeEnum.REMINDER_2));
            if (this.noOfReminderSent == 1) {
                int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL);
                int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL);
                DateTime quotationSharedDate = proposalAggregate.getInceptionOn();
                DateTime secondReminderDateTime = quotationSharedDate.plusDays(firstReminderDay + secondReminderDay);
                ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new ILProposalReminderEvent(event.getProposalId()));
                scheduledTokens.add(secondReminderScheduleToken);
            }
        }
    }

    @SagaEventHandler(associationProperty = "proposalId")
    public void handle(ILProposalClosureEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Proposal Closure Event .....", event);
        }
        IndividualLifePolicy proposalAggregate = ilPolicyMongoRepository.load(event.getProposalId());
        commandGateway.send(new CreateProposalNotificationCommand(event.getProposalId(), RolesUtil.INDIVIDUAL_LIFE__PROPOSAL_PROCESSOR_ROLE, LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.PROPOSAL,
                WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.CANCELLATION));
        if (PolicyStatus.IN_FORCE.equals(proposalAggregate.getPolicyStatus())) {
            commandGateway.send(new ILProposalClosureCommand(event.getProposalId()));
        }
    }

}
