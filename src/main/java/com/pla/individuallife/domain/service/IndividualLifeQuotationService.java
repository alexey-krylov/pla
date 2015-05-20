package com.pla.individuallife.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.domain.model.quotation.*;
import com.pla.individuallife.query.ProposedAssuredDto;
import com.pla.individuallife.query.ProposerDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 5/13/2015.
 */
@DomainService
public class IndividualLifeQuotationService {

    private ILQuotationRoleAdapter ilQuotationRoleAdapter;

    private ILQuotationNumberGenerator ilQuotationNumberGenerator;

    @Autowired
    public IndividualLifeQuotationService(ILQuotationRoleAdapter ilquotationRoleAdapter, ILQuotationNumberGenerator ilquotationNumberGenerator) {
        this.ilQuotationRoleAdapter = ilquotationRoleAdapter;
        this.ilQuotationNumberGenerator = ilquotationNumberGenerator;
    }

    public IndividualLifeQuotation createQuotation(AgentId agentId, String assuredTitle, String assuredFName, String assuredSurname, String assuredNRC, PlanId planId,  UserDetails userDetails) {
        ILQuotationProcessor IlQuotationProcessor = ilQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber("5", "2", IndividualLifeQuotation.class);
        return IlQuotationProcessor.createIndividualLifeQuotation(quotationNumber, IlQuotationProcessor.getUserName(), quotationId, agentId,  assuredTitle,  assuredFName,  assuredSurname,  assuredNRC, planId);
    }

    public IndividualLifeQuotation updateWithProposer(IndividualLifeQuotation individualLifeQuotation, ProposerDto proposerDto, AgentId agentId, UserDetails userDetails) {
        ILQuotationProcessor IlQuotationProcessor = ilQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerDto.getProposerTitle(), proposerDto.getProposerFName(), proposerDto.getProposerSurname(), proposerDto.getProposerNRC(), proposerDto.getDateOfBirth(), proposerDto.getAgeNextBirthDay(), proposerDto.getGender(), proposerDto.getMobileNumber(), proposerDto.getEmailId() );
        return IlQuotationProcessor.updateWithProposerAndAgentId(individualLifeQuotation, proposerBuilder.build(), agentId);
    }

    public IndividualLifeQuotation updateWithAssured(IndividualLifeQuotation individualLifeQuotation, ProposedAssuredDto proposedAssuredDto,Boolean isAssuredTheProposer, UserDetails userDetails) {
        ILQuotationProcessor IlQuotationProcessor = ilQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.getAssuredBuilder(proposedAssuredDto.getAssuredTitle(), proposedAssuredDto.getAssuredFName(), proposedAssuredDto.getAssuredSurname(), proposedAssuredDto.getAssuredNRC(), proposedAssuredDto.getDateOfBirth(), proposedAssuredDto.getAgeNextBirthDay(), proposedAssuredDto.getGender(), proposedAssuredDto.getMobileNumber(), proposedAssuredDto.getEmailId(), proposedAssuredDto.getOccupation());
        return IlQuotationProcessor.updateWithAssured(individualLifeQuotation, proposedAssuredBuilder.build(), isAssuredTheProposer);
    }

    private IndividualLifeQuotation checkQuotationNeedForVersioningAndGetQuotation(ILQuotationProcessor ILQuotationProcessor, IndividualLifeQuotation individualLifeQuotation) {
        if (!individualLifeQuotation.requireVersioning()) {
            return individualLifeQuotation;
        }
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber("5", "2", IndividualLifeQuotation.class);
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        return individualLifeQuotation.cloneQuotation(quotationNumber, ILQuotationProcessor.getUserName(), quotationId);
    }


}
