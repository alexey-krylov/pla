package com.pla.grouplife.policy.application.command;

import com.google.common.collect.Sets;
import com.pla.grouplife.policy.domain.model.GroupLifePolicy;
import com.pla.grouplife.policy.domain.service.GLPolicyFactory;
import com.pla.grouplife.sharedresource.model.vo.GLProposerDocument;
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
public class GLPolicyCommandHandler {

    private GLPolicyFactory glPolicyFactory;

    private Repository<GroupLifePolicy> glPolicyMongoRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    public GLPolicyCommandHandler(GLPolicyFactory glPolicyFactory, Repository<GroupLifePolicy> glPolicyMongoRepository) {
        this.glPolicyFactory = glPolicyFactory;
        this.glPolicyMongoRepository = glPolicyMongoRepository;
    }

    @CommandHandler
    public void createPolicy(GLProposalToPolicyCommand proposalToPolicyCommand) {
        GroupLifePolicy groupLifePolicy = glPolicyFactory.createPolicy(proposalToPolicyCommand.getProposalId());
        glPolicyMongoRepository.add(groupLifePolicy);
    }


    @CommandHandler
    public void memberDeleteCommandHandler(GroupLifePolicyMemberDeletionCommand groupLifePolicyMemberDeletionCommand) {
        GroupLifePolicy groupLifePolicy = glPolicyMongoRepository.load(new PolicyId(groupLifePolicyMemberDeletionCommand.getPolicyId().getPolicyId()));
        groupLifePolicy  = groupLifePolicy.updateWithDeletedMember(groupLifePolicyMemberDeletionCommand.getDeletedFamilyIds());
    }


    @CommandHandler
    public void uploadMandatoryDocument(GLPolicyDocumentCommand glPolicyDocumentCommand) throws IOException {
        GroupLifePolicy groupLifePolicy = glPolicyMongoRepository.load(new PolicyId(glPolicyDocumentCommand.getPolicyId()));
        String fileName = glPolicyDocumentCommand.getFile() != null ? glPolicyDocumentCommand.getFile().getOriginalFilename() : "";
        Set<GLProposerDocument> documents = groupLifePolicy.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(glPolicyDocumentCommand.getFile().getInputStream(), fileName, glPolicyDocumentCommand.getFile().getContentType()).getId().toString();
        GLProposerDocument currentDocument = new GLProposerDocument(glPolicyDocumentCommand.getDocumentId(), fileName, gridFsDocId, glPolicyDocumentCommand.getFile().getContentType(), glPolicyDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GLProposerDocument existingDocument = documents.stream().filter(new Predicate<GLProposerDocument>() {
                @Override
                public boolean test(GLProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(glPolicyDocumentCommand.getFilename(), gridFsDocId, glPolicyDocumentCommand.getFile().getContentType());
        }
        groupLifePolicy = groupLifePolicy.updateWithDocuments(documents);
    }


}
