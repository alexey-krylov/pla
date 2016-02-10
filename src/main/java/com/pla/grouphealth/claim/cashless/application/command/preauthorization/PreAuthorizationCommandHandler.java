package com.pla.grouphealth.claim.cashless.application.command.preauthorization;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.GHClaimDocumentCommand;
import com.pla.grouphealth.claim.cashless.repository.preauthorization.PreAuthorizationRequestRepository;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/4/2016.
 */
@Component
public class PreAuthorizationCommandHandler {
    @Autowired
    PreAuthorizationService preAuthorizationService;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @CommandHandler
    public int uploadPreAuthorizationDetails(UploadPreAuthorizationCommand uploadPreAuthorizationCommand) throws GenerateReminderFollowupException {
        return preAuthorizationService.uploadPreAuthorizationDetails(uploadPreAuthorizationCommand);
    }

    @CommandHandler
    public void uploadMandatoryDocument(GHClaimDocumentCommand ghClaimDocumentCommand) throws IOException {
        String fileName = ghClaimDocumentCommand.getFile() != null ? ghClaimDocumentCommand.getFile().getOriginalFilename() : "";
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestRepository.findByPreAuthorizationRequestId(ghClaimDocumentCommand.getPreAuthorizationRequestId());
        preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.EVALUATION);
        Set<GHProposerDocument> documents = preAuthorizationRequest.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(ghClaimDocumentCommand.getFile().getInputStream(), fileName, ghClaimDocumentCommand.getFile().getContentType()).getId().toString();
        GHProposerDocument currentDocument = new GHProposerDocument(ghClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId, ghClaimDocumentCommand.getFile().getContentType(), ghClaimDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GHProposerDocument existingDocument = documents.stream().filter(new Predicate<GHProposerDocument>() {
                @Override
                public boolean test(GHProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument.updateWithNameAndContent(fileName, gridFsDocId, ghClaimDocumentCommand.getFile().getContentType());
        }
        preAuthorizationRequest.updateWithDocuments(documents);
        Set<AdditionalDocument> additionalDocuments = preAuthorizationRequest.getAdditionalRequiredDocumentsByUnderwriter();
        if(isNotEmpty(additionalDocuments)) {
            additionalDocuments =  markAdditionalRequiredDocumentSubmitted(preAuthorizationRequest.getAdditionalRequiredDocumentsByUnderwriter(), ghClaimDocumentCommand.getDocumentId());
            preAuthorizationRequest.updateAdditionalRequiredDocuments(additionalDocuments);
            preAuthorizationRequest.updateRequirementEmailSentFlag(Boolean.FALSE);
        }
        preAuthorizationRequestRepository.save(preAuthorizationRequest);
    }

    private Set<AdditionalDocument> markAdditionalRequiredDocumentSubmitted(Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter, String documentId) {
        return additionalRequiredDocumentsByUnderwriter.stream().map(document -> {
            if(document.getDocumentCode().trim().equals(documentId.trim())){
                document.setHasSubmitted(Boolean.TRUE);
            }
            return document;
        }).collect(Collectors.toSet());
    }
}