package com.pla.grouphealth.application.command.quotation;

import com.pla.grouphealth.domain.model.quotation.GroupHealthQuotation;
import com.pla.grouphealth.domain.service.GroupHealthQuotationService;

import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Component
public class GHQuotationCommandHandler {

    private Repository<GroupHealthQuotation> ghQuotationMongoRepository;

    private GroupHealthQuotationService groupHealthQuotationService;

    @Autowired
    public GHQuotationCommandHandler(Repository<GroupHealthQuotation> glQuotationMongoRepository, GroupHealthQuotationService groupHealthQuotationService) {
        this.ghQuotationMongoRepository = ghQuotationMongoRepository;
        this.groupHealthQuotationService = groupHealthQuotationService;
    }

    @CommandHandler
    public String createQuotation(CreateGHQuotationCommand createGHQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = groupHealthQuotationService.createQuotation(createGHQuotationCommand.getAgentId(), createGHQuotationCommand.getProposerName(), createGHQuotationCommand.getUserDetails());
        ghQuotationMongoRepository.add(groupHealthQuotation);
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateGHQuotationWithProposerCommand updateGHQuotationWithProposerCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGHQuotationWithProposerCommand.getQuotationId()));
        groupHealthQuotation = groupHealthQuotationService.updateWithProposer(groupHealthQuotation, updateGHQuotationWithProposerCommand.getProposerDto(), updateGHQuotationWithProposerCommand.getUserDetails());
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAgentDetail(UpdateGHQuotationWithAgentCommand updateGHQuotationWithAgentCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGHQuotationWithAgentCommand.getQuotationId()));
        groupHealthQuotation = groupHealthQuotationService.updateWithAgent(groupHealthQuotation, updateGHQuotationWithAgentCommand.getAgentId(), updateGHQuotationWithAgentCommand.getUserDetails());
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }
}
