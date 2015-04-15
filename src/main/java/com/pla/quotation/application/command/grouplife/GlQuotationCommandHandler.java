package com.pla.quotation.application.command.grouplife;

import com.pla.quotation.domain.model.grouplife.GroupLifeQuotation;
import com.pla.quotation.domain.service.GroupLifeQuotationService;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 4/15/2015.
 */
@Component
public class GlQuotationCommandHandler {

    private Repository<GroupLifeQuotation> glQuotationMongoRepository;

    private GroupLifeQuotationService groupLifeQuotationService;

    @Autowired
    public GlQuotationCommandHandler(Repository<GroupLifeQuotation> glQuotationMongoRepository, GroupLifeQuotationService groupLifeQuotationService) {
        this.glQuotationMongoRepository = glQuotationMongoRepository;
        this.groupLifeQuotationService = groupLifeQuotationService;
    }

    @CommandHandler
    public String createQuotation(CreateGLQuotationCommand createGLQuotationCommand) {
        GroupLifeQuotation groupLifeQuotation = groupLifeQuotationService.createQuotation(createGLQuotationCommand.getAgentId(), createGLQuotationCommand.getProposerName(), createGLQuotationCommand.getUserDetails());
        glQuotationMongoRepository.add(groupLifeQuotation);
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateGLQuotationWithProposerCommand updateGLQuotationWithProposerCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithProposerCommand.getQuotationId()));
        groupLifeQuotation = groupLifeQuotationService.updateWithProposer(groupLifeQuotation, updateGLQuotationWithProposerCommand.getProposerDto(), updateGLQuotationWithProposerCommand.getUserDetails());
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAgentDetail(UpdateGLQuotationWithAgentCommand updateGLQuotationWithAgentCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithAgentCommand.getQuotationId()));
        groupLifeQuotation = groupLifeQuotationService.updateWithAgent(groupLifeQuotation, updateGLQuotationWithAgentCommand.getAgentId(), updateGLQuotationWithAgentCommand.getUserDetails());
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }
}
