package com.pla.individuallife.application.command.quotation;

import com.pla.individuallife.domain.model.quotation.IndividualLifeQuotation;
import com.pla.individuallife.domain.service.IndividualLifeQuotationService;

import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Component
public class ILQuotationCommandHandler {

    private Repository<IndividualLifeQuotation> ilQuotationMongoRepository;

    private IndividualLifeQuotationService individualLifeQuotationService;

    @Autowired
    public ILQuotationCommandHandler(Repository<IndividualLifeQuotation> ilQuotationMongoRepository, IndividualLifeQuotationService individualLifeQuotationService) {
        this.ilQuotationMongoRepository = ilQuotationMongoRepository;
        this.individualLifeQuotationService = individualLifeQuotationService;
    }

    @CommandHandler
    public String createQuotation(CreateILQuotationCommand createILQuotationCommand) {
        IndividualLifeQuotation individualLifeQuotation = individualLifeQuotationService.createQuotation(createILQuotationCommand.getAgentId(), createILQuotationCommand.getAssuredTitle(), createILQuotationCommand.getAssuredFName(), createILQuotationCommand.getAssuredSurame(), createILQuotationCommand.getAssuredNRC(), createILQuotationCommand.getPlanId(), createILQuotationCommand.getUserDetails());
        ilQuotationMongoRepository.add(individualLifeQuotation);
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateILQuotationWithProposerCommand updateILQuotationWithProposerCommand) {
        IndividualLifeQuotation individualLifeQuotation = ilQuotationMongoRepository.load(new QuotationId(updateILQuotationWithProposerCommand.getQuotationId()));
        individualLifeQuotation = individualLifeQuotationService.updateWithProposer(individualLifeQuotation, updateILQuotationWithProposerCommand.getProposerDto(), updateILQuotationWithProposerCommand.getAgentId(), updateILQuotationWithProposerCommand.getUserDetails());
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAssured(UpdateILQuotationWithAssuredCommand updateILQuotationWithProposerCommand) {
        IndividualLifeQuotation individualLifeQuotation = ilQuotationMongoRepository.load(new QuotationId(updateILQuotationWithProposerCommand.getQuotationId()));
        individualLifeQuotation = individualLifeQuotationService.updateWithAssured(individualLifeQuotation, updateILQuotationWithProposerCommand.getProposedAssuredDto(), updateILQuotationWithProposerCommand.getIsAssuredTheProposer(), updateILQuotationWithProposerCommand.getUserDetails());
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

}
