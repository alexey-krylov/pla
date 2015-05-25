package com.pla.individuallife.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.domain.model.quotation.*;
import com.pla.individuallife.query.ProposedAssuredDto;
import com.pla.individuallife.query.ProposerDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 5/13/2015.
 */
@DomainService
public class IndividualLifeQuotationService {

    private ILQuotationRoleAdapter ilQuotationRoleAdapter;

    private ILQuotationNumberGenerator ilQuotationNumberGenerator;

    private IIdGenerator idGenerator;

    @Autowired
    public IndividualLifeQuotationService(ILQuotationRoleAdapter ilquotationRoleAdapter, ILQuotationNumberGenerator ilquotationNumberGenerator, IIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.ilQuotationRoleAdapter = ilquotationRoleAdapter;
        this.ilQuotationNumberGenerator = ilquotationNumberGenerator;
    }

    public IndividualLifeQuotation createQuotation(AgentId agentId, String assuredId, String assuredTitle, String assuredFName, String assuredSurname, String assuredNRC, PlanId planId,  UserDetails userDetails) {
        ILQuotationProcessor IlQuotationProcessor = ilQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        QuotationId quotationId = new QuotationId(idGenerator.nextId());
        assuredId = idGenerator.nextId();
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber("5", "2", IndividualLifeQuotation.class);
        return IlQuotationProcessor.createIndividualLifeQuotation(quotationNumber, IlQuotationProcessor.getUserName(), quotationId, agentId, assuredId, assuredTitle, assuredFName, assuredSurname, assuredNRC, planId);
    }

    public IndividualLifeQuotation updateWithProposer(IndividualLifeQuotation individualLifeQuotation, ProposerDto proposerDto, AgentId agentId, UserDetails userDetails) {
        String proposerId = null;
        ILQuotationProcessor IlQuotationProcessor = ilQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        if(individualLifeQuotation.getProposer() == null) { proposerId = idGenerator.nextId(); }
                                                          else
                                                          { proposerId = individualLifeQuotation.getProposer().getProposerId(); }
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerId, proposerDto.getProposerTitle(), proposerDto.getProposerFName(), proposerDto.getProposerSurname(), proposerDto.getProposerNRC(), proposerDto.getDateOfBirth(), proposerDto.getAgeNextBirthDay(), proposerDto.getGender(), proposerDto.getMobileNumber(), proposerDto.getEmailId() );
        return IlQuotationProcessor.updateWithProposerAndAgentId(individualLifeQuotation, proposerBuilder.build(), agentId, proposerId);
    }

    public IndividualLifeQuotation updateWithAssured(IndividualLifeQuotation individualLifeQuotation, ProposedAssuredDto proposedAssuredDto,Boolean isAssuredTheProposer, UserDetails userDetails) {
        String proposerId = null;
        ILQuotationProcessor IlQuotationProcessor = ilQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.getAssuredBuilder(proposedAssuredDto.getAssuredId(), proposedAssuredDto.getAssuredTitle(), proposedAssuredDto.getAssuredFName(), proposedAssuredDto.getAssuredSurname(), proposedAssuredDto.getAssuredNRC(), proposedAssuredDto.getDateOfBirth(), proposedAssuredDto.getAgeNextBirthDay(), proposedAssuredDto.getGender(), proposedAssuredDto.getMobileNumber(), proposedAssuredDto.getEmailId(), proposedAssuredDto.getOccupation());
        if (isAssuredTheProposer && individualLifeQuotation.getProposer() == null ) { proposerId = idGenerator.nextId(); }
        return IlQuotationProcessor.updateWithAssured(individualLifeQuotation, proposedAssuredBuilder.build(), isAssuredTheProposer, proposerId);
    }

    private IndividualLifeQuotation checkQuotationNeedForVersioningAndGetQuotation(ILQuotationProcessor ILQuotationProcessor, IndividualLifeQuotation individualLifeQuotation) {
        if (!individualLifeQuotation.requireVersioning()) {
            return individualLifeQuotation;
        }
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber("5", "2", IndividualLifeQuotation.class);
        QuotationId quotationId = new QuotationId(idGenerator.nextId());
        return individualLifeQuotation.cloneQuotation(quotationNumber, ILQuotationProcessor.getUserName(), quotationId);
    }


}
