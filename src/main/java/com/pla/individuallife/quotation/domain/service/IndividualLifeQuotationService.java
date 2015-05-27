package com.pla.individuallife.quotation.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.model.*;
import com.pla.individuallife.quotation.presentation.dto.PlanDetailDto;
import com.pla.individuallife.quotation.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.quotation.presentation.dto.ProposerDto;
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

    private RoleAdapter roleAdapter;

    private ILQuotationNumberGenerator ilQuotationNumberGenerator;

    private IIdGenerator idGenerator;

    @Autowired
    public IndividualLifeQuotationService(RoleAdapter roleAdapter, ILQuotationNumberGenerator ilquotationNumberGenerator, IIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.roleAdapter = roleAdapter;
        this.ilQuotationNumberGenerator = ilquotationNumberGenerator;
    }

    public IndividualLifeQuotation createQuotation(AgentId agentId, String assuredId, String assuredTitle, String assuredFName, String assuredSurname, String assuredNRC, PlanId planId, UserDetails userDetails) {
        ILQuotationProcessor IlQuotationProcessor = roleAdapter.userToQuotationProcessor(userDetails);
        QuotationId quotationId = new QuotationId(idGenerator.nextId());
        assuredId = idGenerator.nextId();
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber("5", "2", IndividualLifeQuotation.class);
        return IlQuotationProcessor.createIndividualLifeQuotation(quotationNumber, IlQuotationProcessor.getUserName(), quotationId, agentId, assuredId, assuredTitle, assuredFName, assuredSurname, assuredNRC, planId);
    }

    public IndividualLifeQuotation updateWithProposer(IndividualLifeQuotation individualLifeQuotation, ProposerDto proposerDto, AgentId agentId, UserDetails userDetails) {
        String proposerId = null;
        ILQuotationProcessor IlQuotationProcessor = roleAdapter.userToQuotationProcessor(userDetails);
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        if (individualLifeQuotation.getProposer() == null) {
            proposerId = idGenerator.nextId();
        } else {
            proposerId = individualLifeQuotation.getProposer().getProposerId();
        }
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerId, proposerDto.getProposerTitle(), proposerDto.getProposerFName(), proposerDto.getProposerSurname(), proposerDto.getProposerNRC(), proposerDto.getDateOfBirth(), proposerDto.getAgeNextBirthDay(), proposerDto.getGender(), proposerDto.getMobileNumber(), proposerDto.getEmailId());
        return IlQuotationProcessor.updateWithProposerAndAgentId(individualLifeQuotation, proposerBuilder.build(), agentId, proposerId);
    }

    public IndividualLifeQuotation updateWithAssured(IndividualLifeQuotation individualLifeQuotation, ProposedAssuredDto proposedAssuredDto, Boolean isAssuredTheProposer, UserDetails userDetails) {
        String proposerId = null;
        ILQuotationProcessor IlQuotationProcessor = roleAdapter.userToQuotationProcessor(userDetails);
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.getAssuredBuilder(proposedAssuredDto.getAssuredId(), proposedAssuredDto.getAssuredTitle(), proposedAssuredDto.getAssuredFName(), proposedAssuredDto.getAssuredSurname(), proposedAssuredDto.getAssuredNRC(), proposedAssuredDto.getDateOfBirth(), proposedAssuredDto.getAgeNextBirthDay(), proposedAssuredDto.getGender(), proposedAssuredDto.getMobileNumber(), proposedAssuredDto.getEmailId(), proposedAssuredDto.getOccupation());
        if (isAssuredTheProposer && individualLifeQuotation.getProposer() == null) {
            proposerId = idGenerator.nextId();
        }
        return IlQuotationProcessor.updateWithAssured(individualLifeQuotation, proposedAssuredBuilder.build(), isAssuredTheProposer, proposerId);
    }

    public IndividualLifeQuotation updateWithPlan(IndividualLifeQuotation individualLifeQuotation, PlanDetailDto planDetailDto, UserDetails userDetails) {
        ILQuotationProcessor IlQuotationProcessor = roleAdapter.userToQuotationProcessor(userDetails);
        if (planDetailDto.getPlanDetailId() == null) planDetailDto.setPlanDetailId(idGenerator.nextId());
        if (planDetailDto.getRiderDetails() != null) {
            planDetailDto.getRiderDetails().stream().filter(riderdetail -> riderdetail.getRiderDetailId() == null).forEach(riderdetail -> riderdetail.setRiderDetailId(idGenerator.nextId()));
        }
        individualLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(IlQuotationProcessor, individualLifeQuotation);
        PlanDetailBuilder planDetailBuilder = PlanDetail.getPlanDetailBuilder(planDetailDto.getPlanDetailId(), planDetailDto.getPlanId(), planDetailDto.getPolicyTerm(), planDetailDto.getPremiumPaymentTerm(), planDetailDto.getSumAssured(), planDetailDto.getRiderDetails());
        return IlQuotationProcessor.updateWithPlan(individualLifeQuotation, planDetailBuilder.build());
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
