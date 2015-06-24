package com.pla.individuallife.proposal.application.command;

import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
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
        ProposalAggregate aggregate = new ProposalAggregate(cmd.getUserDetails(), new ProposalId(cmd.getProposalId()), proposalNumber, proposedAssured, getProposer(cmd));
        ilProposalMongoRepository.add(aggregate);
    }



    private Proposer getProposer(ILCreateProposalCommand proposalCommand) {
        Proposer proposer = new ProposerBuilder()
                .withOtherName(proposalCommand.getProposer().getOtherName())
                .withDateOfBirth(proposalCommand.getProposer().getDateOfBirth())
                .withEmailAddress(proposalCommand.getProposer().getEmailAddress())
                .withFirstName(proposalCommand.getProposer().getFirstName())
                .withSurname(proposalCommand.getProposer().getSurname())
                .withTitle(proposalCommand.getProposer().getTitle())
                .withDateOfBirth(proposalCommand.getProposer().getDateOfBirth())
                .withGender(proposalCommand.getProposer().getGender())
                .withMobileNumber(proposalCommand.getProposer().getMobileNumber())
                .withMaritalStatus(proposalCommand.getProposer().getMaritalStatus())
                .withSpouseEmailAddress(proposalCommand.getProposer().getSpouse().getEmailAddress())
                .withSpouseFirstName(proposalCommand.getProposer().getSpouse().getFirstName())
                .withMobileNumber(proposalCommand.getProposer().getSpouse().getMobileNumber())
                        .withSpouseLastName(proposalCommand.getProposer().getSpouse().getSurname())
                .withNrc(proposalCommand.getProposer().getNrc())
                .withMaritalStatus(proposalCommand.getProposer().getMaritalStatus())
                .withEmploymentDetail(new EmploymentDetailBuilder()
                        .withEmploymentDate(proposalCommand.getProposer().getEmployment().getEmploymentDate())
                        .withEmploymentTypeId(proposalCommand.getProposer().getEmployment().getEmploymentType())
                        .withEmployer(proposalCommand.getProposer().getEmployment().getEmployer())
                        .withWorkPhone(proposalCommand.getProposer().getEmployment().getWorkPhone())
                        .withAddress(new AddressBuilder()
                                .withAddress1(proposalCommand.getProposer().getEmployment().getAddress1())
                                .withAddress2(proposalCommand.getProposer().getEmployment().getAddress2())
                                .withProvince(proposalCommand.getProposer().getEmployment().getProvince())
                                .withTown(proposalCommand.getProposer().getEmployment().getTown()).createAddress())
                        .withOccupationClass(proposalCommand.getProposer().getEmployment().getOccupation()).createEmploymentDetail())
                .withResidentialAddress(new ResidentialAddress(new AddressBuilder()
                        .withAddress1(proposalCommand.getProposer().getResidentialAddress().getAddress1())
                        .withAddress2(proposalCommand.getProposer().getResidentialAddress().getAddress2())
                        .withProvince(proposalCommand.getProposer().getResidentialAddress().getProvince())
                        .withPostalCode(proposalCommand.getProposer().getResidentialAddress().getPostalCode())
                        .withTown(proposalCommand.getProposer().getResidentialAddress().getTown()).createAddress(),
                        proposalCommand.getProposer().getResidentialAddress().getHomePhone(),
                        proposalCommand.getProposer().getResidentialAddress().getEmailAddress())).createProposer();
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
                .withIsProposer(proposalCommand.getProposedAssured().isProposer())
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
   public void createCompulsoryQuestion(ILCreateQuestionCommand createQuestionCommand)
   {
       ProposalAggregate proposalAggregate = null;
       ProposalId proposalId=new ProposalId(createQuestionCommand.getProposalId());
       FamilyPersonalDetail familyPersonalDetail=createQuestionCommand.getFamilyPersonalDetail();

       try{
           proposalAggregate=ilProposalMongoRepository.load(proposalId);
           proposalAggregate.updateFamilyPersonalDetail(familyPersonalDetail);
       }
       catch (AggregateNotFoundException e) {
           e.printStackTrace();
       }
   }
}
