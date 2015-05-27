package com.pla.individuallife.quotation.application.command;

import com.pla.individuallife.quotation.domain.model.IndividualLifeQuotation;
import com.pla.individuallife.quotation.domain.service.IndividualLifeQuotationService;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Component
public class ILQuotationCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private Repository<IndividualLifeQuotation> ilQuotationMongoRepository;


    private IndividualLifeQuotationService individualLifeQuotationService;

    @Autowired
    public ILQuotationCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, IndividualLifeQuotationService individualLifeQuotationService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.individualLifeQuotationService = individualLifeQuotationService;
    }

    @CommandHandler
    public String createQuotation(CreateILQuotationCommand createILQuotationCommand) {
        IndividualLifeQuotation individualLifeQuotation = individualLifeQuotationService.createQuotation( createILQuotationCommand.getAgentId(), createILQuotationCommand.getAssuredId(), createILQuotationCommand.getAssuredTitle(), createILQuotationCommand.getAssuredFName(), createILQuotationCommand.getAssuredSurame(), createILQuotationCommand.getAssuredNRC(), createILQuotationCommand.getPlanId(), createILQuotationCommand.getUserDetails());
        JpaRepository<IndividualLifeQuotation, QuotationId> individualLifeQuotationJpaRepository = jpaRepositoryFactory.getCrudRepository(IndividualLifeQuotation.class);
        individualLifeQuotationJpaRepository.save(individualLifeQuotation);
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateILQuotationWithProposerCommand updateILQuotationWithProposerCommand) {
        JpaRepository<IndividualLifeQuotation, QuotationId> individualLifeQuotationJpaRepository = jpaRepositoryFactory.getCrudRepository(IndividualLifeQuotation.class);
        IndividualLifeQuotation individualLifeQuotation = individualLifeQuotationJpaRepository.findOne((new QuotationId(updateILQuotationWithProposerCommand.getQuotationId())));
        individualLifeQuotation = individualLifeQuotationService.updateWithProposer(individualLifeQuotation, updateILQuotationWithProposerCommand.getProposerDto(), updateILQuotationWithProposerCommand.getAgentId(), updateILQuotationWithProposerCommand.getUserDetails());
        individualLifeQuotationJpaRepository.save(individualLifeQuotation);
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAssured(UpdateILQuotationWithAssuredCommand updateILQuotationWithProposerCommand) {
        JpaRepository<IndividualLifeQuotation, QuotationId> individualLifeQuotationJpaRepository = jpaRepositoryFactory.getCrudRepository(IndividualLifeQuotation.class);
        IndividualLifeQuotation individualLifeQuotation = individualLifeQuotationJpaRepository.findOne((new QuotationId(updateILQuotationWithProposerCommand.getQuotationId())));
        individualLifeQuotation = individualLifeQuotationService.updateWithAssured(individualLifeQuotation, updateILQuotationWithProposerCommand.getProposedAssuredDto(), updateILQuotationWithProposerCommand.getIsAssuredTheProposer(), updateILQuotationWithProposerCommand.getUserDetails());
        individualLifeQuotationJpaRepository.save(individualLifeQuotation);
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithPlan(UpdateILQuotationWithPlanCommand updateILQuotationWithPlanCommand) {
        JpaRepository<IndividualLifeQuotation, QuotationId> individualLifeQuotationJpaRepository = jpaRepositoryFactory.getCrudRepository(IndividualLifeQuotation.class);
        IndividualLifeQuotation individualLifeQuotation = individualLifeQuotationJpaRepository.findOne((new QuotationId(updateILQuotationWithPlanCommand.getQuotationId())));
        individualLifeQuotation = individualLifeQuotationService.updateWithPlan(individualLifeQuotation, updateILQuotationWithPlanCommand.getPlanDetailDto(), updateILQuotationWithPlanCommand.getUserDetails());
        individualLifeQuotationJpaRepository.save(individualLifeQuotation);
        return individualLifeQuotation.getIdentifier().getQuotationId();
    }


}
