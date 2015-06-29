package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.proposal.presentation.dto.ProposerDto;
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
        ProposedAssured proposedAssured = getProposedAssured(cmd);
        if (logger.isDebugEnabled()) {
            logger.debug(" ProposedAssured :: " + proposedAssured);
        }
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        ProposalAggregate aggregate = new ProposalAggregate(cmd.getUserDetails(), new ProposalId(cmd.getProposalId()), proposalNumber, proposedAssured, cmd.getAgentCommissionDetails());
        ilProposalMongoRepository.add(aggregate);
    }

    @CommandHandler
    public void updateProposalProposer(ILProposalUpdateWithProposerCommand cmd) {
        Proposer proposer = getProposer(cmd.getProposer());
        if (logger.isDebugEnabled()) {
            logger.debug(" Proposer :: " + proposer);
        }
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate.updateWithProposer(aggregate, proposer, cmd.getUserDetails());
        ilProposalMongoRepository.add(aggregate);
    }


    private Proposer getProposer(ProposerDto dto) {
        Proposer proposer = new ProposerBuilder()
                .withOtherName(dto.getOtherName())
                .withDateOfBirth(dto.getDateOfBirth())
                .withEmailAddress(dto.getEmailAddress())
                .withFirstName(dto.getFirstName())
                .withSurname(dto.getSurname())
                .withTitle(dto.getTitle())
                .withDateOfBirth(dto.getDateOfBirth())
                .withGender(dto.getGender())
                .withMobileNumber(dto.getMobileNumber())
                .withMaritalStatus(dto.getMaritalStatus())
                .withSpouseEmailAddress(dto.getSpouse().getEmailAddress())
                .withSpouseFirstName(dto.getSpouse().getFirstName())
                .withMobileNumber(dto.getSpouse().getMobileNumber())
                        .withSpouseLastName(dto.getSpouse().getSurname())
                .withNrc(dto.getNrc())
                .withMaritalStatus(dto.getMaritalStatus())
                .withEmploymentDetail(new EmploymentDetailBuilder()
                        .withEmploymentDate(dto.getEmployment().getEmploymentDate())
                        .withEmploymentTypeId(dto.getEmployment().getEmploymentType())
                        .withEmployer(dto.getEmployment().getEmployer())
                        .withWorkPhone(dto.getEmployment().getWorkPhone())
                        .withAddress(new AddressBuilder()
                                .withAddress1(dto.getEmployment().getAddress1())
                                .withAddress2(dto.getEmployment().getAddress2())
                                .withProvince(dto.getEmployment().getProvince())
                                .withTown(dto.getEmployment().getTown()).createAddress())
                        .withOccupationClass(dto.getEmployment().getOccupation()).createEmploymentDetail())
                .withResidentialAddress(new ResidentialAddress(new AddressBuilder()
                        .withAddress1(dto.getResidentialAddress().getAddress1())
                        .withAddress2(dto.getResidentialAddress().getAddress2())
                        .withProvince(dto.getResidentialAddress().getProvince())
                        .withPostalCode(dto.getResidentialAddress().getPostalCode())
                        .withTown(dto.getResidentialAddress().getTown()).createAddress(),
                        dto.getResidentialAddress().getHomePhone(),
                        dto.getResidentialAddress().getEmailAddress())).createProposer();
        return proposer;
    }

    public ProposedAssured getProposedAssured(ILCreateProposalCommand proposalCommand) {
        ProposedAssured proposedAssured = new ProposedAssuredBuilder()
                .withOtherName(proposalCommand.getProposedAssured().getOtherName())
                .withTitle(proposalCommand.getProposedAssured().getTitle())
                .withFirstName(proposalCommand.getProposedAssured().getFirstName())
                .withSurname(proposalCommand.getProposedAssured().getSurname())
                .withNrc(proposalCommand.getProposedAssured().getNrc())
                .withDateOfBirth(proposalCommand.getProposedAssured().getDateOfBirth())
                .withGender(proposalCommand.getProposedAssured().getGender())
                .withMobileNumber(proposalCommand.getProposedAssured().getMobileNumber())
                .withEmailAddress(proposalCommand.getProposedAssured().getEmailAddress())
                .withMaritalStatus(proposalCommand.getProposedAssured().getMaritalStatus())
                .withSpouseFirstName(proposalCommand.getProposedAssured().getSpouse().getFirstName())
                .withMobileNumber(proposalCommand.getProposedAssured().getSpouse().getMobileNumber())
                .withSpouseLastName(proposalCommand.getProposedAssured().getSpouse().getSurname())
                .withSpouseEmailAddress(proposalCommand.getProposedAssured().getSpouse().getEmailAddress())
                .withIsProposer(proposalCommand.getProposedAssured().getIsProposer())
                .withEmploymentDetail(new EmploymentDetailBuilder()
                        .withEmployer(proposalCommand.getProposedAssured().getEmployment().getEmployer())
                        .withEmploymentDate(proposalCommand.getProposedAssured().getEmployment().getEmploymentDate())
                        .withEmploymentTypeId(proposalCommand.getProposedAssured().getEmployment().getEmploymentType())
                        .withWorkPhone(proposalCommand.getProposedAssured().getEmployment().getWorkPhone())
                        .withAddress(new AddressBuilder()
                                .withAddress1(proposalCommand.getProposedAssured().getEmployment().getAddress1())
                                .withAddress2(proposalCommand.getProposedAssured().getEmployment().getAddress2())
                                .withProvince(proposalCommand.getProposedAssured().getEmployment().getProvince())
                                .withTown(proposalCommand.getProposedAssured().getEmployment().getTown()).createAddress())
                        .withOccupationClass(proposalCommand.getProposedAssured().getEmployment().getOccupation()).createEmploymentDetail())
                .withResidentialAddress(new ResidentialAddress(new AddressBuilder()
                        .withAddress1(proposalCommand.getProposedAssured().getResidentialAddress().getAddress1())
                        .withAddress2(proposalCommand.getProposedAssured().getResidentialAddress().getAddress2())
                        .withProvince(proposalCommand.getProposedAssured().getResidentialAddress().getProvince())
                        .withPostalCode(proposalCommand.getProposedAssured().getResidentialAddress().getPostalCode())
                        .withTown(proposalCommand.getProposedAssured().getResidentialAddress().getTown()).createAddress(),
                        proposalCommand.getProposedAssured().getResidentialAddress().getHomePhone(), proposalCommand.getProposedAssured().getResidentialAddress().getEmailAddress())).createProposedAssured();

        return proposedAssured;
    }


    @CommandHandler
    public void updateCompulsoryHealthStatement(ILUpdateCompulsoryHealthStatementCommand updateCompulsoryHealthStatementCommand) {
        ProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(updateCompulsoryHealthStatementCommand.getProposalId());
        List<QuestionAnswer> questionAnswerDtoList=updateCompulsoryHealthStatementCommand.getQuestions();
        try {
            proposalAggregate = ilProposalMongoRepository.load(proposalId);
            proposalAggregate.updateCompulsoryHealthStatement(questionAnswerDtoList);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
   public void updateFamilyPersonalDetails(ILUpdateFamilyPersonalDetailsCommand cmd)
   {
       ProposalAggregate proposalAggregate = null;
       ProposalId proposalId=new ProposalId(cmd.getProposalId());
       FamilyPersonalDetail familyPersonalDetail=cmd.getFamilyPersonalDetail();

       try{
           proposalAggregate=ilProposalMongoRepository.load(proposalId);
           proposalAggregate.updateFamilyPersonalDetail(familyPersonalDetail);
       }
       catch (AggregateNotFoundException e) {
           e.printStackTrace();
       }
   }

     @CommandHandler
    public void updateWithPlanDetail(ILProposalUpdateWithPlanAndBeneficiariesCommand cmd)
    {
        ProposalAggregate aggregate = null;
        ProposalId proposalId=new ProposalId(cmd.getProposalId());

        try{
            aggregate=ilProposalMongoRepository.load(proposalId);
            aggregate.updatePlan(aggregate, cmd.getProposalPlanDetail(), cmd.getBeneficiaries(),  cmd.getUserDetails());
            ilProposalMongoRepository.add(aggregate);
        }
        catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }


}
