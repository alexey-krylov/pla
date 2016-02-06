package com.pla.grouphealth.claim.cashless.application.command.preauthorization;

import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.exception.RoutingLevelNotFoundException;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
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
    public String updatePreAuthorizationRequest(UpdatePreAuthorizationCommand updatePreAuthorizationCommand) throws GenerateReminderFollowupException, RoutingLevelNotFoundException {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = updatePreAuthorizationCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, updatePreAuthorizationCommand.getUserName());
        preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.EVALUATION)
                .updateWithProcessorUserId(updatePreAuthorizationCommand.getUserName());
        if(preAuthorizationClaimantDetailCommand.isSubmitEventFired()) {
            RoutingLevel routingLevel = updatePreAuthorizationCommand.getRoutingLevel();
            if(routingLevel.equals(RoutingLevel.UNDERWRITING_LEVEL_ONE))
                preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1);
            if(routingLevel.equals(RoutingLevel.UNDERWRITING_LEVEL_TWO))
                preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1);
            preAuthorizationRequest
                    .updatePreAuthorizationSubmitted(Boolean.TRUE)
                    .updateWithSubmittedDate(LocalDate.now());
        }
        return preAuthorizationRequest.getIdentifier();
    }

    @CommandHandler
    public boolean approvePreAuthorization(ApprovePreAuthorizationCommand approvePreAuthorizationCommand){
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = approvePreAuthorizationCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, approvePreAuthorizationCommand.getUserName());
        preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.APPROVED);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean rejectPreAuthorization(RejectPreAuthorizationCommand rejectPreAuthorizationCommand){
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = rejectPreAuthorizationCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, rejectPreAuthorizationCommand.getUserName());
        preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.REJECTED);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean returnByUnderwriter(ReturnPreAuthorizationCommand returnPreAuthorizationCommand) {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = returnPreAuthorizationCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, returnPreAuthorizationCommand.getUserName());
        preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.RETURNED);
        preAuthorizationRequest.updatePreAuthorizationSubmitted(Boolean.FALSE);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean routeToSeniorUnderwriter(RoutePreAuthorizationCommand routePreAuthorizationCommand) {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = routePreAuthorizationCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, routePreAuthorizationCommand.getUserName());
        /*
        * logic to get the senior underwriter userId
        * preAuthorizationRequest.updateWithPreAuthorizationUnderWriterUserId(userId);
        * */
        preAuthorizationRequest
                .updateWithPreAuthorizationUnderWriterUserId(null)
                .updateWithUnderWriterRoutedToSeniorUnderWriterUserId(routePreAuthorizationCommand.getUserName())
                .updateStatus(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL2);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean addRequirementToPreAuthorization(AddRequirementPreAuthorizationCommand addRequirementPreAuthorizationCommand) throws GenerateReminderFollowupException {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = addRequirementPreAuthorizationCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, addRequirementPreAuthorizationCommand.getUserName());
        //preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.RETURNED);
        preAuthorizationRequest.savedRegisterFollowUpReminders();
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean underwriterPreAuthorizationUpdate(UnderwriterPreAuthorizationUpdateCommand underwriterPreAuthorizationUpdateCommand) throws GenerateReminderFollowupException {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = underwriterPreAuthorizationUpdateCommand.getPreAuthorizationClaimantDetailCommand();
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId ,"PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRequestId);
        preAuthorizationRequest = populateDetailsToPreAuthorization(preAuthorizationClaimantDetailCommand, preAuthorizationRequest, underwriterPreAuthorizationUpdateCommand.getUserName());
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean removeAdditionalDocument(PreAuthorizationRemoveAdditionalDocumentCommand preAuthorizationRemoveAdditionalDocumentCommand) {
        boolean result = Boolean.FALSE;
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(preAuthorizationRemoveAdditionalDocumentCommand.getPreAuthorizationId());
        if(isNotEmpty(preAuthorizationRequest)){
            Set<GHProposerDocument> ghProposerDocuments = preAuthorizationRequest.getProposerDocuments();
            result =  preAuthorizationRequestService.removeDocumentByGridFsDocId(ghProposerDocuments, preAuthorizationRemoveAdditionalDocumentCommand.getGridFsDocId());
        }
        return result;
    }

    private PreAuthorizationRequest populateDetailsToPreAuthorization(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, PreAuthorizationRequest preAuthorizationRequest, String userName) {
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
                .updateWithComments(preAuthorizationClaimantDetailCommand.getCommentDetails(), userName)
                .updateWithAdditionalRequirementAskedFor(preAuthorizationClaimantDetailCommand.getAdditionalRequiredDocuments());
        return preAuthorizationRequest;
    }
}
