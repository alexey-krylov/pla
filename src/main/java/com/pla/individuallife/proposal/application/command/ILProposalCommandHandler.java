package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.sharedkernel.identifier.ProposalId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Prasant on 26-May-15.
 */
@Component
public class ILProposalCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(ILProposalCommandHandler.class);
    @Autowired
    private ProposalNumberGenerator proposalNumberGenerator;
    @Autowired
    private Repository<ProposalAggregate> ilProposalMongoRepository;
    @Autowired
    private ILQuotationFinder ilQuotationFinder;

    @CommandHandler
    public void createProposal(ILCreateProposalCommand cmd) {
        ProposedAssured proposedAssured = ProposedAssuredBuilder.getProposedAssuredBuilder(cmd.getProposedAssured()).createProposedAssured();
        if (logger.isDebugEnabled()) {
            logger.debug(" ProposedAssured :: " + proposedAssured);
        }
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        ProposalAggregate aggregate = new ProposalAggregate(cmd.getUserDetails(), new ProposalId(cmd.getProposalId()), proposalNumber, proposedAssured, cmd.getAgentCommissionDetails());
        ilProposalMongoRepository.add(aggregate);
    }

    @CommandHandler
    public void updateProposalProposer(ILProposalUpdateWithProposerCommand cmd) {
        Proposer proposer = ProposerBuilder.getProposerBuilder(cmd.getProposer()).createProposer();
        if (logger.isDebugEnabled()) {
            logger.debug(" Proposer :: " + proposer);
        }
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate.updateWithProposer(aggregate, proposer, cmd.getUserDetails());
        ilProposalMongoRepository.add(aggregate);
    }




    @CommandHandler
    public void updateGeneralDetails(ILProposalUpdateGeneralDetailsCommand cmd) {
        ProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        List<QuestionDto> dto = cmd.getGenerateDetails();
        try {
            proposalAggregate = ilProposalMongoRepository.load(proposalId);
            proposalAggregate.updateGeneralDetails(dto);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void updateAdditionalDetails(ILProposalUpdateAdditionalDetailsCommand cmd) {
        ProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        try {
            proposalAggregate = ilProposalMongoRepository.load(proposalId);
            proposalAggregate.updateAdditionalDetails(cmd.getMedicalAttendantDetails(), cmd.getMedicalAttendantDuration(), cmd.getDateAndReason(), cmd.getReplacementDetails());
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void updateCompulsoryHealthStatement(ILProposalUpdateCompulsoryHealthStatementCommand updateCompulsoryHealthStatementCommand) {
        ProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(updateCompulsoryHealthStatementCommand.getProposalId());
        List<QuestionDto> questionDtoList=updateCompulsoryHealthStatementCommand.getCompulsoryHealthDetails();
        try {
            proposalAggregate = ilProposalMongoRepository.load(proposalId);
            proposalAggregate.updateCompulsoryHealthStatement(questionDtoList);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void updateFamilyPersonalDetails(ILProposalUpdateFamilyPersonalDetailsCommand cmd) {
        ProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        FamilyPersonalDetail familyPersonalDetail = cmd.getFamilyPersonalDetail();

        try {
            proposalAggregate = ilProposalMongoRepository.load(proposalId);
            proposalAggregate.updateFamilyPersonalDetail(familyPersonalDetail);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void updateWithPlanDetail(ILProposalUpdateWithPlanAndBeneficiariesCommand cmd) {
        ProposalAggregate aggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());

        try {
            aggregate = ilProposalMongoRepository.load(proposalId);
            aggregate.updatePlan(aggregate, cmd.getProposalPlanDetail(), cmd.getBeneficiaries(), cmd.getUserDetails());
            ilProposalMongoRepository.add(aggregate);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }


}
