package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.CommentDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 1/4/2016.
 */
@Component
public class PreAuthorizationRequestCommandHandler {
    @Autowired
    PreAuthorizationRequestService preAuthorizationRequestService;
    @Autowired
    private Repository<PreAuthorizationRequest> preAuthorizationRequestMongoRepository;

    @CommandHandler
    public String createPreAuthorizationRequest(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) throws GenerateReminderFollowupException {
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest
                .updateWithPreAuthorizationDate(preAuthorizationClaimantDetailCommand.getPreAuthorizationDate())
                .updateWithCategory(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithRelationship(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithClaimType(preAuthorizationClaimantDetailCommand.getClaimType())
                .updateWithClaimIntimationDate(preAuthorizationClaimantDetailCommand.getClaimIntimationDate())
                .updateWithBatchNumber(preAuthorizationClaimantDetailCommand.getBatchNumber())
                .updateWithProposerDetail(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithPreAuthorizationRequestPolicyDetail(preAuthorizationClaimantDetailCommand)
                .updateWithPreAuthorizationRequestHCPDetail(preAuthorizationClaimantDetailCommand.getClaimantHCPDetailDto())
                .updateWithPreAuthorizationRequestDiagnosisTreatmentDetail(preAuthorizationClaimantDetailCommand.getDiagnosisTreatmentDtos())
                .updateWithPreAuthorizationRequestIllnessDetail(preAuthorizationClaimantDetailCommand.getIllnessDetailDto())
                .updateWithPreAuthorizationRequestDrugService(preAuthorizationClaimantDetailCommand.getDrugServicesDtos())
                .updateStatus(PreAuthorizationRequest.Status.EVALUATION);
        if(preAuthorizationClaimantDetailCommand.isSubmitEventFired())
            preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.UNDERWRITING);
        return preAuthorizationRequest.getIdentifier();
    }

    @CommandHandler
    public Set<CommentDetail> updateComments(UpdateCommentCommand updateCommentCommand){
        return preAuthorizationRequestService.updateComments(updateCommentCommand);
    }
}
