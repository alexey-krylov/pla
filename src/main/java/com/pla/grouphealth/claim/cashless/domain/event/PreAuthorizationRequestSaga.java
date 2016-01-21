package com.pla.grouphealth.claim.cashless.domain.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.grouphealth.claim.cashless.application.command.CreateClaimNotificationCommand;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequest;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 1/13/2016.
 */
@Component
public class PreAuthorizationRequestSaga extends AbstractAnnotatedSaga {
    private transient static final Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationRequestSaga.class);

    private transient EventScheduler eventScheduler;
    private transient CommandGateway commandGateway;
    private GenericMongoRepository<PreAuthorizationRequest> preAuthorizationRequestMongoRepository;
    private transient IProcessInfoAdapter processInfoAdapter;
    PreAuthorizationRequestService preAuthorizationRequestService;
    private Map<String, ScheduleToken> scheduledTokens = Maps.newLinkedHashMap();

    @Autowired
    public PreAuthorizationRequestSaga(EventScheduler eventScheduler, CommandGateway commandGateway, GenericMongoRepository<PreAuthorizationRequest> preAuthorizationRequestMongoRepository, IProcessInfoAdapter processInfoAdapter, PreAuthorizationRequestService preAuthorizationRequestService){
        this.eventScheduler = eventScheduler;
        this.commandGateway = commandGateway;
        this.preAuthorizationRequestMongoRepository = preAuthorizationRequestMongoRepository;
        this.processInfoAdapter = processInfoAdapter;
        this.preAuthorizationRequestService = preAuthorizationRequestService;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "preAuthorizationRequestId")
    public void handle(PreAuthorizationFollowUpReminderEvent event) throws ProcessInfoException {
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        int secondReminderDay = processInfoAdapter.getDaysForSecondReminder(LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM);
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(event.getPreAuthorizationRequestId());
        DateTime preAuthCreatedDate = preAuthorizationRequest.getCreatedOn();
        DateTime firstReminderDateTime = preAuthCreatedDate.plusDays(firstReminderDay);
        DateTime purgeScheduleDateTime = preAuthCreatedDate.plusDays(noOfDaysToPurge);
        DateTime secondReminderDateTime = preAuthCreatedDate.plusDays(firstReminderDay + secondReminderDay);
        DateTime closureScheduleDateTime = preAuthCreatedDate.plusDays(firstReminderDay + secondReminderDay + noOfDaysToClosure);
        ScheduleToken firstReminderScheduleToken = eventScheduler.schedule(firstReminderDateTime, new PreAuthorizationFirstReminderEvent(event.getPreAuthorizationRequestId()));
        ScheduleToken secondReminderScheduleToken = eventScheduler.schedule(secondReminderDateTime, new PreAuthorizationSecondReminderEvent(event.getPreAuthorizationRequestId()));
        ScheduleToken closureScheduleToken = eventScheduler.schedule(closureScheduleDateTime, new PreAuthorizationClosureEvent(event.getPreAuthorizationRequestId()));
        scheduledTokens.put("firstReminderScheduleToken", firstReminderScheduleToken);
        scheduledTokens.put("secondReminderScheduleToken", secondReminderScheduleToken);
        scheduledTokens.put("closureScheduleToken", closureScheduleToken);
        preAuthorizationRequest.updateWithScheduledTokens(scheduledTokens);
    }

    @SagaEventHandler(associationProperty = "preAuthorizationRequestId")
    public void handle(PreAuthorizationFirstReminderEvent event){
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(event.getPreAuthorizationRequestId());
        if(!preAuthorizationRequest.isFirstReminderSent() && !preAuthorizationRequest.isSecondReminderSent()) {
            List<String> pendingDocumentList = getPendingDocumentList(preAuthorizationRequest);
            if(pendingDocumentList.size() > 0) {
                commandGateway.send(new CreateClaimNotificationCommand(event.getPreAuthorizationRequestId(), RolesUtil.GROUP_HEALTH_PRE_AUTHORIZATION_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM, WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_1, pendingDocumentList));
                preAuthorizationRequest.updateFlagForFirstReminderSent(Boolean.TRUE);
            }
        }
    }

    private List<String> getPendingDocumentList(PreAuthorizationRequest preAuthorizationRequest) {
        List<GHProposalMandatoryDocumentDto> mandatoryDocuments = null;
        try {
            String familyId = isEmpty(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail()) ? isNotEmpty(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail().getAssuredDetail()) ? preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail().getAssuredDetail().getClientId() : null : null;
            Assert.notNull(familyId, "Error sending Pre-Auth first reminder");
            mandatoryDocuments = preAuthorizationRequestService.findMandatoryDocuments(new FamilyId(familyId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getNameListOfPendingMandatoryDocument(mandatoryDocuments, preAuthorizationRequest.getProposerDocuments());
    }

    @SagaEventHandler(associationProperty = "preAuthorizationRequestId")
    public void handle(PreAuthorizationSecondReminderEvent event){
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(event.getPreAuthorizationRequestId());
        if(preAuthorizationRequest.isFirstReminderSent() && !preAuthorizationRequest.isSecondReminderSent()) {
            List<String> pendingDocumentList = getPendingDocumentList(preAuthorizationRequest);
            if(pendingDocumentList.size() > 0) {
                commandGateway.send(new CreateClaimNotificationCommand(event.getPreAuthorizationRequestId(), RolesUtil.GROUP_HEALTH_PRE_AUTHORIZATION_PROCESSOR_ROLE, LineOfBusinessEnum.GROUP_HEALTH, ProcessType.CLAIM, WaitingForEnum.MANDATORY_DOCUMENTS, ReminderTypeEnum.REMINDER_2, pendingDocumentList));
                preAuthorizationRequest.updateFlagForSecondReminderSent(Boolean.TRUE);
            }
        }
    }

    @SagaEventHandler(associationProperty = "preAuthorizationRequestId")
    public void handle(PreAuthorizationClosureEvent event){
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(event.getPreAuthorizationRequestId());
        List<String> pendingDocumentList = getPendingDocumentList(preAuthorizationRequest);
        if(preAuthorizationRequest.isFirstReminderSent() && preAuthorizationRequest.isSecondReminderSent() && pendingDocumentList.size() > 0) {
            preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.CANCELLED);
        }
    }


    private List<String> getNameListOfPendingMandatoryDocument(List<GHProposalMandatoryDocumentDto> mandatoryDocuments, Set<GHProposerDocument> proposerDocuments) {
        List<String> documents = getDocumentNameList(mandatoryDocuments);
        Assert.notEmpty(documents, "Error sending Pre-Auth first reminder, no document set up found for the given client.");
        if(isEmpty(proposerDocuments)){
            return documents;
        }
        return checkAndFindDocumentNotSubmitted(proposerDocuments, documents);
    }

    private List<String> checkAndFindDocumentNotSubmitted(Set<GHProposerDocument> proposerDocuments, List<String> documents) {
        List<String> uploadedDocuments = proposerDocuments.parallelStream().map(GHProposerDocument::getDocumentName).collect(Collectors.toList())
                .parallelStream().map(String::trim).collect(Collectors.toList());
        return documents.parallelStream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return !uploadedDocuments.contains(s.trim());
            }
        }).collect(Collectors.toList());
    }

    private List<String> getDocumentNameList(List<GHProposalMandatoryDocumentDto> mandatoryDocuments) {
        return isNotEmpty(mandatoryDocuments) ? mandatoryDocuments.parallelStream().map(GHProposalMandatoryDocumentDto::getDocumentName).collect(Collectors.toList()) : Lists.newArrayList();
    }
}
