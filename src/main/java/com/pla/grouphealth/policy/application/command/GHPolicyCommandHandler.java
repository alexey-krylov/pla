package com.pla.grouphealth.policy.application.command;

import com.google.common.collect.Sets;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.domain.service.GHPolicyFactory;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.sharedkernel.identifier.PolicyId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
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
 * Created by Samir on 7/9/2015.
 */
@Component
public class GHPolicyCommandHandler {

    private GHPolicyFactory ghPolicyFactory;

    private Repository<GroupHealthPolicy> ghPolicyMongoRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    public GHPolicyCommandHandler(GHPolicyFactory ghPolicyFactory, Repository<GroupHealthPolicy> ghPolicyMongoRepository) {
        this.ghPolicyFactory = ghPolicyFactory;
        this.ghPolicyMongoRepository = ghPolicyMongoRepository;
    }

    @CommandHandler
    public void createPolicy(GHProposalToPolicyCommand proposalToPolicyCommand) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyFactory.createPolicy(proposalToPolicyCommand.getProposalId());
        ghPolicyMongoRepository.add(groupHealthPolicy);
    }


    @CommandHandler
    public void uploadMandatoryDocument(GHPolicyDocumentCommand ghPolicyDocumentCommand) throws IOException {
        String fileName = ghPolicyDocumentCommand.getFile() != null ? ghPolicyDocumentCommand.getFile().getOriginalFilename() : "";
        GroupHealthPolicy groupHealthPolicy = ghPolicyMongoRepository.load(new PolicyId(ghPolicyDocumentCommand.getPolicyId()));
        Set<GHProposerDocument> documents = groupHealthPolicy.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(ghPolicyDocumentCommand.getFile().getInputStream(), fileName, ghPolicyDocumentCommand.getFile().getContentType()).getId().toString();
        GHProposerDocument currentDocument = new GHProposerDocument(ghPolicyDocumentCommand.getDocumentId(), fileName, gridFsDocId, ghPolicyDocumentCommand.getFile().getContentType(), ghPolicyDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GHProposerDocument existingDocument = documents.stream().filter(new Predicate<GHProposerDocument>() {
                @Override
                public boolean test(GHProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(fileName, gridFsDocId, ghPolicyDocumentCommand.getFile().getContentType());
        }
        groupHealthPolicy = groupHealthPolicy.updateWithDocuments(documents);
    }
}
