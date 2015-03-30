package com.pla.core.application;

import com.pla.core.domain.exception.MandatoryDocumentException;
import com.pla.core.domain.model.MandatoryDocument;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.service.MandatoryDocumentService;
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
public class MandatoryDocumentCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private MandatoryDocumentService mandatoryDocumentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MandatoryDocumentCommandHandler.class);

    @Autowired
     public MandatoryDocumentCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, MandatoryDocumentService mandatoryDocumentService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.mandatoryDocumentService = mandatoryDocumentService;
    }

    @CommandHandler
    public void createMandatoryDocumentHandler(CreateMandatoryDocumentCommand createMandatoryDocumentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + createMandatoryDocumentCommand);
        }

        JpaRepository<MandatoryDocument, String> coverageRepository = jpaRepositoryFactory.getCrudRepository(MandatoryDocument.class);
        MandatoryDocument mandatoryDocument = mandatoryDocumentService.createMandatoryDocument(createMandatoryDocumentCommand.getPlanId(), createMandatoryDocumentCommand.getCoverageId(), createMandatoryDocumentCommand.getProcess(), createMandatoryDocumentCommand.getDocuments(), createMandatoryDocumentCommand.getUserDetails());
        try {
            coverageRepository.save(mandatoryDocument);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving mandatory document failed*****", e);
            throw new MandatoryDocumentException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateMandatoryDocumentHandler(UpdateMandatoryDocumentCommand updateMandatoryDocumentCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + updateMandatoryDocumentCommand);
        }
        JpaRepository<MandatoryDocument, String> coverageRepository = jpaRepositoryFactory.getCrudRepository(MandatoryDocument.class);
        MandatoryDocument mandatoryDocument =  coverageRepository.findOne(updateMandatoryDocumentCommand.getId());
        mandatoryDocument = mandatoryDocumentService.updateMandatoryDocument(mandatoryDocument, updateMandatoryDocumentCommand.getDocuments(), updateMandatoryDocumentCommand.getUserDetails());
        try {
            coverageRepository.save(mandatoryDocument);
        } catch (RuntimeException e) {
            LOGGER.error("*****update mandatory document failed*****", e);
            throw new MandatoryDocumentException(e.getMessage());
        }
    }

}
