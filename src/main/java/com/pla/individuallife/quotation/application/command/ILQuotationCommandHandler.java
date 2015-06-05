package com.pla.individuallife.quotation.application.command;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.model.IndividualLifeQuotation;
import com.pla.individuallife.quotation.domain.service.IndividualLifeQuotationService;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Component
public class ILQuotationCommandHandler {

    private Repository<IndividualLifeQuotation> ilQuotationJpaRepository;


    private IndividualLifeQuotationService individualLifeQuotationService;

    private IPremiumCalculator premiumCalculator;

    @Autowired
    public ILQuotationCommandHandler(Repository<IndividualLifeQuotation> ilQuotationJpaRepository, IndividualLifeQuotationService individualLifeQuotationService, IPremiumCalculator premiumCalculator) {
        this.ilQuotationJpaRepository = ilQuotationJpaRepository;
        this.individualLifeQuotationService = individualLifeQuotationService;
        this.premiumCalculator = premiumCalculator;
    }

    @CommandHandler
    public String createQuotation(CreateILQuotationCommand cmd) {
        IndividualLifeQuotation individualLifeQuotation = individualLifeQuotationService.createQuotation(new AgentId(cmd.getAgentId()), cmd.getTitle(), cmd.getFirstName(),
                cmd.getSurname(), cmd.getNrcNumber(), new PlanId(cmd.getPlanId()), cmd.getUserDetails());
        ilQuotationJpaRepository.add(individualLifeQuotation);
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateILQuotationWithProposerCommand cmd) {
        IndividualLifeQuotation individualLifeQuotation = ilQuotationJpaRepository.load((new QuotationId(cmd.getQuotationId())));
        IndividualLifeQuotation individualLifeQuotation2 = individualLifeQuotationService.updateWithProposer(individualLifeQuotation, cmd.getProposerDto(), new AgentId(cmd.getAgentId()), cmd.getUserDetails());
        if (individualLifeQuotation.equals(individualLifeQuotation))
            return individualLifeQuotation.getIdentifier().getQuotationId();
        else {
            ilQuotationJpaRepository.add(individualLifeQuotation2);
            return individualLifeQuotation2.getIdentifier().getQuotationId();
        }
    }

    @CommandHandler
    public String updateWithAssured(UpdateILQuotationWithAssuredCommand updateILQuotationWithAssuredCommand) {
        IndividualLifeQuotation individualLifeQuotation = ilQuotationJpaRepository.load((new QuotationId(updateILQuotationWithAssuredCommand.getQuotationId())));
        IndividualLifeQuotation individualLifeQuotation2 = individualLifeQuotationService.updateWithAssured(individualLifeQuotation, updateILQuotationWithAssuredCommand.getProposedAssured(), updateILQuotationWithAssuredCommand.getIsAssuredTheProposer(), updateILQuotationWithAssuredCommand.getUserDetails());
        if (individualLifeQuotation.equals(individualLifeQuotation))
            return individualLifeQuotation.getIdentifier().getQuotationId();
        else {
            ilQuotationJpaRepository.add(individualLifeQuotation2);
            return individualLifeQuotation2.getIdentifier().getQuotationId();
        }
    }

    @CommandHandler
    public String updateWithPlan(UpdateILQuotationWithPlanCommand cmd) {
        IndividualLifeQuotation individualLifeQuotation = ilQuotationJpaRepository.load((new QuotationId(cmd.getQuotationId())));
        IndividualLifeQuotation individualLifeQuotation2 = individualLifeQuotationService.updateWithPlan(individualLifeQuotation, cmd.getPlanDetailDto(), cmd.getUserDetails());
        if (individualLifeQuotation.equals(individualLifeQuotation))
            return individualLifeQuotation.getIdentifier().getQuotationId();
        else {
            ilQuotationJpaRepository.add(individualLifeQuotation2);
            return individualLifeQuotation2.getIdentifier().getQuotationId();
        }
    }


    @CommandHandler
    public String generateQuotation(ILGenerateQuotationCommand cmd) {
        IndividualLifeQuotation individualLifeQuotation = ilQuotationJpaRepository.load((new QuotationId(cmd.getQuotationId())));
        individualLifeQuotation.generateQuotation(LocalDate.now());
        return individualLifeQuotation.getQuotationId().toString();
    }
}
