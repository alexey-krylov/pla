package com.pla.grouphealth.claim.cashless.application.command;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.presentation.dto.GHClaimDocumentCommand;
import com.pla.grouphealth.policy.application.command.GHPolicyDocumentCommand;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.sharedkernel.identifier.PolicyId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Mohan Sharma on 1/4/2016.
 */
@Component
public class PreAuthorizationCommandHandler {
    @Autowired
    PreAuthorizationService preAuthorizationService;
    @Autowired
    private GenericMongoRepository<PreAuthorizationRequest> preAuthorizationRequestMongoRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @CommandHandler
    public int uploadPreAuthorizationDetails(UploadPreAuthorizationCommand uploadPreAuthorizationCommand){
        return preAuthorizationService.uploadPreAuthorizationDetails(uploadPreAuthorizationCommand);
    }

    @CommandHandler
    public void uploadMandatoryDocument(GHClaimDocumentCommand ghClaimDocumentCommand) throws IOException {
        String fileName = ghClaimDocumentCommand.getFile() != null ? ghClaimDocumentCommand.getFile().getOriginalFilename() : "";
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestMongoRepository.load(new PolicyId(ghClaimDocumentCommand.getPolicyId()));
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
        preAuthorizationRequest = preAuthorizationRequest.updateWithDocuments(documents);
    }
}
