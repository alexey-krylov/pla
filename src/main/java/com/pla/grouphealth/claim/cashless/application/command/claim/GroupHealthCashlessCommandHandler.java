package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.application.service.claim.GroupHealthCashlessClaimService;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBatch;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.claim.cashless.repository.preauthorization.PreAuthorizationRequestRepository;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.*;
import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.Status.*;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 2/3/2016.
 */
@Component
public class GroupHealthCashlessCommandHandler {
    @Autowired
    private GroupHealthCashlessClaimService groupHealthCashlessClaimService;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    PreAuthorizationService preAuthorizationService;
    @Autowired
    SequenceGenerator sequenceGenerator;
    @Autowired
    private Repository<GroupHealthCashlessClaim> groupHealthCashlessClaimAxonRepository;
    @Autowired
    private PreAuthorizationRequestService preAuthorizationRequestService;

    @CommandHandler
    public void createClaim(UploadGroupHealthCashlessClaimCommand uploadGroupHealthCashlessClaimCommand){
        List<List<ClaimUploadedExcelDataDto>> refurbishedSet = preAuthorizationService.createSubListBasedOnSimilarCriteria(uploadGroupHealthCashlessClaimCommand.getClaimUploadedExcelDataDtos());
        notEmpty(refurbishedSet, "Error uploading no PreAuthorization data list found to save");
        String batchNumber = sequenceGenerator.getSequence(GroupHealthCashlessClaimBatch.class);
        batchNumber = String.format("%08d", Integer.parseInt(batchNumber.trim()));
        final String finalBatchNumber = batchNumber;
        List<GroupHealthCashlessClaim> groupHealthCashlessClaimList = refurbishedSet.stream().map(new Function<List<ClaimUploadedExcelDataDto>, GroupHealthCashlessClaim>() {
            @Override
            public GroupHealthCashlessClaim apply(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
                return groupHealthCashlessClaimService.constructGroupHealthCashlessClaimEntity(claimUploadedExcelDataDtos, uploadGroupHealthCashlessClaimCommand.getBatchDate(), uploadGroupHealthCashlessClaimCommand.getBatchUploaderUserId(), uploadGroupHealthCashlessClaimCommand.getHcpCode(), finalBatchNumber);
            }
        }).collect(Collectors.toList());
        if (isNotEmpty(groupHealthCashlessClaimList)) {
            groupHealthCashlessClaimList.stream().forEach(claim -> {
                groupHealthCashlessClaimAxonRepository.add(claim);
                try {
                    claim.savedRegisterFollowUpReminders();
                } catch (GenerateReminderFollowupException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @CommandHandler
    public boolean updateGroupHealthCashlessClaim(UpdateGroupHealthCashlessClaimCommand updateGroupHealthCashlessClaimCommand){
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = updateGroupHealthCashlessClaimCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"GroupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim = groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        groupHealthCashlessClaim.updateStatus(EVALUATION)
                .updateWithClaimProcessorUserId(updateGroupHealthCashlessClaimCommand.getUserName());
        if(groupHealthCashlessClaimDto.isSubmitEventFired()) {
            if(groupHealthCashlessClaimService.checkIfServiceMismatched(groupHealthCashlessClaim)){
                markSubmittedAndChangeStatus(groupHealthCashlessClaim, SERVICE_MISMATCHED);
                return Boolean.TRUE;
            }
            if(groupHealthCashlessClaimService.checkIfBillMismatched(groupHealthCashlessClaim)){
                markSubmittedAndChangeStatus(groupHealthCashlessClaim, BILL_MISMATCHED);
                return Boolean.TRUE;
            }
            RoutingLevel routingLevel = updateGroupHealthCashlessClaimCommand.getRoutingLevel();
            if(routingLevel.equals(RoutingLevel.UNDERWRITING_LEVEL_ONE)) {
                markSubmittedAndChangeStatus(groupHealthCashlessClaim, UNDERWRITING_LEVEL1);
            }
            if(routingLevel.equals(RoutingLevel.UNDERWRITING_LEVEL_TWO)) {
                markSubmittedAndChangeStatus(groupHealthCashlessClaim, UNDERWRITING_LEVEL2);
            }
        }
        return Boolean.TRUE;
    }

    @CommandHandler
    public void uploadMandatoryDocument(GroupHealthCashlessClaimDocumentCommand groupHealthCashlessClaimDocumentCommand) throws IOException {
        String fileName = groupHealthCashlessClaimDocumentCommand.getFile() != null ? groupHealthCashlessClaimDocumentCommand.getFile().getOriginalFilename() : "";
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimDocumentCommand.getGroupHealthCashlessClaimId());
        groupHealthCashlessClaim.updateStatus(EVALUATION);
        Set<GHProposerDocument> documents = groupHealthCashlessClaim.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(groupHealthCashlessClaimDocumentCommand.getFile().getInputStream(), fileName, groupHealthCashlessClaimDocumentCommand.getFile().getContentType()).getId().toString();
        GHProposerDocument currentDocument = new GHProposerDocument(groupHealthCashlessClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId, groupHealthCashlessClaimDocumentCommand.getFile().getContentType(), groupHealthCashlessClaimDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GHProposerDocument existingDocument = documents.stream().filter(new Predicate<GHProposerDocument>() {
                @Override
                public boolean test(GHProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument.updateWithNameAndContent(fileName, gridFsDocId, groupHealthCashlessClaimDocumentCommand.getFile().getContentType());
        }
        groupHealthCashlessClaim.updateWithDocuments(documents);
        Set<AdditionalDocument> additionalDocuments = groupHealthCashlessClaim.getAdditionalRequiredDocumentsByUnderwriter();
        if(isNotEmpty(additionalDocuments)) {
            additionalDocuments =  markAdditionalRequiredDocumentSubmitted(groupHealthCashlessClaim.getAdditionalRequiredDocumentsByUnderwriter(), groupHealthCashlessClaimDocumentCommand.getDocumentId());
            groupHealthCashlessClaim.updateWithAdditionalRequiredDocumentsByUnderwriter(additionalDocuments);
            groupHealthCashlessClaim.updateWithAdditionalRequirementEmailSent(Boolean.FALSE);
        }
    }

    @CommandHandler
    public boolean removeAdditionalDocument(GroupHealthCashlessClaimRemoveAdditionalDocumentCommand groupHealthCashlessClaimRemoveAdditionalDocumentCommand) {
        boolean result = Boolean.FALSE;
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimRemoveAdditionalDocumentCommand.getGroupHealthCashlessClaimId());
        if(isNotEmpty(groupHealthCashlessClaim)){
            Set<GHProposerDocument> ghProposerDocuments = groupHealthCashlessClaim.getProposerDocuments();
            result =  preAuthorizationRequestService.removeDocumentByGridFsDocId(ghProposerDocuments, groupHealthCashlessClaimRemoveAdditionalDocumentCommand.getGridFsDocId());
        }
        return result;
    }

    @CommandHandler
    public boolean approvePreAuthorization(ApproveGroupHealthCashlessClaimCommand approveGroupHealthCashlessClaimCommand){
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = approveGroupHealthCashlessClaimCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"groupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim = groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        groupHealthCashlessClaim.updateStatus(APPROVED);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean rejectPreAuthorization(RejectGroupHealthCashlessClaimCommand rejectGroupHealthCashlessClaimCommand){
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = rejectGroupHealthCashlessClaimCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"groupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim = groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        groupHealthCashlessClaim.updateStatus(REPUDIATED);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean returnByUnderwriter(ReturnGroupHealthCashlessClaimCommand returnGroupHealthCashlessClaimCommand) {
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = returnGroupHealthCashlessClaimCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"groupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim = groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        groupHealthCashlessClaim.updateStatus(RETURNED);
        groupHealthCashlessClaim.updateWithSubmittedFlag(Boolean.FALSE);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean routeToSeniorUnderwriter(RouteGroupHealthCashlessClaimCommand routeGroupHealthCashlessClaimCommand) {
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = routeGroupHealthCashlessClaimCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"groupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim = groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        groupHealthCashlessClaim
                .updateWithClaimUnderWriterUserId(null)
                .updateWithUnderWriterRoutedToSeniorUnderWriterUserId(routeGroupHealthCashlessClaimCommand.getUserName())
                .updateStatus(UNDERWRITING_LEVEL2);
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean addRequirementToPreAuthorization(AddRequirementGroupHealthCashlessClaimCommand addRequirementGroupHealthCashlessClaimCommand) throws GenerateReminderFollowupException {
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = addRequirementGroupHealthCashlessClaimCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"groupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim = groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        //preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.RETURNED);
        groupHealthCashlessClaim.savedRegisterFollowUpReminders();
        return Boolean.TRUE;
    }

    @CommandHandler
    public boolean underwriterGroupHealthCashlessClaimUpdate(UnderwriterGroupHealthCashlessClaimUpdateCommand underwriterGroupHealthCashlessClaimUpdateCommand) throws GenerateReminderFollowupException {
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = underwriterGroupHealthCashlessClaimUpdateCommand.getGroupHealthCashlessClaimDto();
        String groupHealthCashlessClaimId = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId();
        notNull(groupHealthCashlessClaimId ,"groupHealthCashlessClaimId is empty for the record");
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimAxonRepository.load(groupHealthCashlessClaimId);
        groupHealthCashlessClaim.populateDetailsToGroupHealthCashlessClaim(groupHealthCashlessClaimDto);
        return Boolean.TRUE;
    }

    private Set<AdditionalDocument> markAdditionalRequiredDocumentSubmitted(Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter, String documentId) {
        return additionalRequiredDocumentsByUnderwriter.stream().map(document -> {
            if(document.getDocumentCode().trim().equals(documentId.trim())){
                document.setHasSubmitted(Boolean.TRUE);
            }
            return document;
        }).collect(Collectors.toSet());
    }

    private void markSubmittedAndChangeStatus(GroupHealthCashlessClaim groupHealthCashlessClaim, Status status) {
        groupHealthCashlessClaim
                .updateStatus(status)
                .updateWithSubmittedFlag(Boolean.TRUE)
                .updateWithSubmissionDate(LocalDate.now());
    }
}