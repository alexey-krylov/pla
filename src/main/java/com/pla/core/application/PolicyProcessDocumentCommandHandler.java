package com.pla.core.application;

import com.pla.core.domain.exception.PolicyProcessDocumentException;
import com.pla.core.domain.model.PolicyProcessDocument;
import com.pla.core.domain.service.PolicyProcessDocumentService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 3/27/2015.
 */
@Component
public class PolicyProcessDocumentCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private PolicyProcessDocumentService policyProcessDocumentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyProcessDocumentCommandHandler.class);

    @Autowired
     public PolicyProcessDocumentCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, PolicyProcessDocumentService policyProcessDocumentService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.policyProcessDocumentService = policyProcessDocumentService ;
    }

    @CommandHandler
    public void createPolicyProcessDocumentHandler(CreatePolicyProcessDocumentCommand createPolicyProcessDocumentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + createPolicyProcessDocumentCommand);
        }
        JpaRepository<PolicyProcessDocument, String> coverageRepository = jpaRepositoryFactory.getCrudRepository(PolicyProcessDocument.class);
        PolicyProcessDocument policyProcessDocument = policyProcessDocumentService.createPolicyProcessDocument(createPolicyProcessDocumentCommand.getPlanId(),createPolicyProcessDocumentCommand.getCoverageId(),createPolicyProcessDocumentCommand.getProcess(),createPolicyProcessDocumentCommand.getDocuments(),createPolicyProcessDocumentCommand.getUserDetails());
        try {
            coverageRepository.save(policyProcessDocument);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving mandatory document failed*****", e);
            throw new PolicyProcessDocumentException(e.getMessage());
        }
    }

    @CommandHandler
    public void updatePolicyProcessDocumentHandler(UpdatePolicyProcessDocumentCommand updatePolicyProcessDocumentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + updatePolicyProcessDocumentCommand);
        }
        JpaRepository<PolicyProcessDocument, String> coverageRepository = jpaRepositoryFactory.getCrudRepository(PolicyProcessDocument.class);
        PolicyProcessDocument policyProcessDocument =  coverageRepository.findOne(updatePolicyProcessDocumentCommand.getId());
        policyProcessDocument = policyProcessDocumentService.updatePolicyProcessDocument(policyProcessDocument,  updatePolicyProcessDocumentCommand.getDocuments(), updatePolicyProcessDocumentCommand.getUserDetails());
        try {
            coverageRepository.save(policyProcessDocument);
        } catch (RuntimeException e) {
            LOGGER.error("*****update mandatory document failed*****", e);
            throw new PolicyProcessDocumentException(e.getMessage());
        }
    }

}
