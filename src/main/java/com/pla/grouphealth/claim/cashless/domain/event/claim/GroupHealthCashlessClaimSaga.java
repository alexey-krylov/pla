package com.pla.grouphealth.claim.cashless.domain.event.claim;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.grouphealth.claim.cashless.application.command.claim.CreateGroupHealthCashlessClaimNotificationCommand;
import com.pla.grouphealth.claim.cashless.application.service.claim.GroupHealthCashlessClaimService;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.util.RolesUtil;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.Status.*;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 1/13/2016.
 */
@Component
@NoArgsConstructor
public class GroupHealthCashlessClaimSaga extends AbstractAnnotatedSaga implements Serializable{
    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupHealthCashlessClaimSaga.class);

    @Autowired
    private transient EventScheduler eventScheduler;
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient GenericMongoRepository<GroupHealthCashlessClaim> groupHealthCashlessClaimAxonRepository;
    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;
    @Autowired
    private transient GroupHealthCashlessClaimService groupHealthCashlessClaimService;
    private Map<String, ScheduleToken> scheduledTokens = Maps.newLinkedHashMap();

    @StartSaga
    @SagaEventHandler(associationProperty = "groupHealthCashlessClaimId")
    public void handle(GroupHealthCashlessClaimFollowUpReminderEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GroupHealthCashlessClaimFollowUpReminderEvent .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(event.getGroupHealthCashlessClaimId());
        DateTime claimCreationDate = groupHealthCashlessClaim.getCreatedOn();
        DateTime firstReminderDateTime = claimCreationDate.plusDays(firstReminderDay);
        DateTime purgeScheduleDateTime = claimCreationDate.plusDays(noOfDaysToPurge);
        DateTime secondReminderDateTime = claimCreationDate.plusDays(firstReminderDay + secondReminderDay);
        DateTime closureScheduleDateTime = claimCreationDate.plusDays(firstReminderDay + secondReminderDay + noOfDaysToClosure);
        ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new GroupHealthCashlessClaimFirstReminderEvent(event.getGroupHealthCashlessClaimId()));
        ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new GroupHealthCashlessClaimSecondReminderEvent(event.getGroupHealthCashlessClaimId()));
        ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new GroupHealthCashlessClaimClosureEvent(event.getGroupHealthCashlessClaimId()));
        scheduledTokens.put("firstReminderScheduleToken", firstReminderScheduleToken);
        scheduledTokens.put("secondReminderScheduleToken", secondReminderScheduleToken);
        scheduledTokens.put("closureScheduleToken", closureScheduleToken);
        groupHealthCashlessClaim.updateWithScheduledTokens(scheduledTokens);
    }

    @SagaEventHandler(associationProperty = "groupHealthCashlessClaimId")
    public void handle(GroupHealthCashlessClaimFirstReminderEvent event){
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(event.getGroupHealthCashlessClaimId());
        if(!groupHealthCashlessClaim.isFirstReminderSent() && !groupHealthCashlessClaim.isSecondReminderSent()) {
            List<String> pendingDocumentList = groupHealthCashlessClaimService.getPendingDocumentList(groupHealthCashlessClaim);
            if(pendingDocumentList.size() > 0) {
                commandGateway.send(new CreateGroupHealthCashlessClaimNotificationCommand(event.getGroupHealthCashlessClaimId(), RolesUtil.GROUP_HEALTH_CASHLESS_CLAIM_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM, WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_1, pendingDocumentList));
                groupHealthCashlessClaim.updateFlagForFirstReminderSent(Boolean.TRUE);
            }
        }
    }

    @SagaEventHandler(associationProperty = "groupHealthCashlessClaimId")
    public void handle(GroupHealthCashlessClaimSecondReminderEvent event){
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(event.getGroupHealthCashlessClaimId());
        if(groupHealthCashlessClaim.isFirstReminderSent() && !groupHealthCashlessClaim.isSecondReminderSent()) {
            List<String> pendingDocumentList = groupHealthCashlessClaimService.getPendingDocumentList(groupHealthCashlessClaim);
            if(pendingDocumentList.size() > 0) {
                commandGateway.send(new CreateGroupHealthCashlessClaimNotificationCommand(event.getGroupHealthCashlessClaimId(), RolesUtil.GROUP_HEALTH_CASHLESS_CLAIM_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM, WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_2, pendingDocumentList));
                groupHealthCashlessClaim.updateFlagForSecondReminderSent(Boolean.TRUE);
            }
        }
    }

    @SagaEventHandler(associationProperty = "groupHealthCashlessClaimId")
    public void handle(GroupHealthCashlessClaimClosureEvent event){
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(event.getGroupHealthCashlessClaimId());
        List<String> pendingDocumentList = groupHealthCashlessClaimService.getPendingDocumentList(groupHealthCashlessClaim);
        if(groupHealthCashlessClaim.isFirstReminderSent() && groupHealthCashlessClaim.isSecondReminderSent() && pendingDocumentList.size() > 0) {
            groupHealthCashlessClaim
                    .updateWithClosedAtLevel(groupHealthCashlessClaim.getStatus().name())
                    .updateStatus(CANCELLED);
        }
    }
}
