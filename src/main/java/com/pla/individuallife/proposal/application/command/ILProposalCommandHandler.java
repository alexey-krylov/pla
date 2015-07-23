package com.pla.individuallife.proposal.application.command;

import com.google.common.collect.Sets;
import com.pla.core.query.AgentFinder;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.Repository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

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
    private ILQuotationFinder quotationFinder;

    @Autowired
    private AgentFinder agentFinder;

    @Autowired
    private PlanFinder planFinder;

    @Autowired
    private ILProposalFinder proposalFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @CommandHandler
    public void createProposal(ILCreateProposalCommand cmd) {

        ProposalAggregate aggregate;
        ProposedAssured proposedAssured = ProposedAssuredBuilder.getProposedAssuredBuilder(cmd.getProposedAssured()).createProposedAssured();
        if (logger.isDebugEnabled()) {
            logger.debug(" ProposedAssured :: " + proposedAssured);
        }
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        if (cmd.getQuotationId() != null) {
            ILQuotationDto dto = quotationFinder.getQuotationById(cmd.getQuotationId());
            Map planDetail = planFinder.findPlanByPlanId(new PlanId(dto.getPlanId()));
            dto.setPlanDetail(planDetail);
            aggregate = new ProposalAggregate(cmd.getUserDetails(), cmd.getProposalId(), proposalNumber, proposedAssured, cmd.getAgentCommissionDetails(), dto, planFinder);
        } else {
            aggregate = new ProposalAggregate(cmd.getUserDetails(), cmd.getProposalId(), proposalNumber, proposedAssured, cmd.getAgentCommissionDetails());
        }
        ilProposalMongoRepository.add(aggregate);
    }

    @CommandHandler
    public void updateProposalProposedAssured(ILUpdateProposalWithProposedAssuredCommand cmd) {

        ProposedAssured proposedAssured = ProposedAssuredBuilder.getProposedAssuredBuilder(cmd.getProposedAssured()).createProposedAssured();
        if (logger.isDebugEnabled()) {
            logger.debug(" ProposedAssured :: " + proposedAssured);
        }
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate.updateWithProposedAssuredAndAgenDetails(cmd.getUserDetails(), cmd.getProposalId(), proposedAssured, cmd.getAgentCommissionDetails());
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
        ProposalAggregate aggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        GeneralDetails gd = new GeneralDetails();
        gd.setAssuredByPLAL(cmd.getAssuredByPLAL());
        gd.setPendingInsuranceByOthers(cmd.getPendingInsuranceByOthers());
        gd.setAssuranceDeclined(cmd.getAssuranceDeclined());
        gd.setAssuredByOthers(cmd.getAssuredByOthers());
        gd.setQuestionAndAnswers(cmd.getGeneralQuestion());
        try {
            aggregate = ilProposalMongoRepository.load(proposalId);
            aggregate.updateGeneralDetails(gd, cmd.getUserDetails());
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
            proposalAggregate.updateAdditionalDetails(cmd.getUserDetails(), cmd.getMedicalAttendantDetails(), cmd.getMedicalAttendantDuration(), cmd.getDateAndReason(), cmd.getReplacementDetails());
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void updateCompulsoryHealthStatement(ILProposalUpdateCompulsoryHealthStatementCommand cmd) {
        ProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        List<QuestionDto> questionDtoList=cmd.getCompulsoryHealthDetails();
        try {
            proposalAggregate = ilProposalMongoRepository.load(proposalId);
            proposalAggregate.updateCompulsoryHealthStatement(questionDtoList, cmd.getUserDetails());
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
            proposalAggregate.updateFamilyPersonalDetail(familyPersonalDetail, cmd.getUserDetails());
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
            aggregate.updatePlan(aggregate, cmd.getProposalPlanDetail(), cmd.getBeneficiaries(), cmd.getUserDetails(), planFinder);
            ilProposalMongoRepository.add(aggregate);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void updateWithPremiumPaymentDetail(ILProposalUpdatePremiumPaymentDetailsCommand cmd) {
        ProposalAggregate aggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());

        try {
            aggregate = ilProposalMongoRepository.load(proposalId);
            aggregate.updateWithPremiumPaymentDetail(aggregate, cmd.getPremiumPaymentDetails(), cmd.getUserDetails());
            ilProposalMongoRepository.add(aggregate);
        } catch (AggregateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler
    public void uploadMandatoryDocument(ILProposalDocumentCommand cmd) throws IOException {
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        Set<ILProposerDocument> documents = aggregate.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(cmd.getFile().getInputStream(), cmd.getFile().getContentType(), cmd.getFilename()).getId().toString();
        ILProposerDocument currentDocument = new ILProposerDocument(cmd.getDocumentId(), cmd.getFilename(), gridFsDocId, cmd.getFile().getContentType());
        if (!documents.add(currentDocument)) {
            ILProposerDocument existingDocument = documents.stream().filter(new Predicate<ILProposerDocument>() {
                @Override
                public boolean test(ILProposerDocument ilProposerDocument) {
                    return currentDocument.equals(ilProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(cmd.getFilename(), gridFsDocId, cmd.getFile().getContentType());
        }
        aggregate.updateWithDocuments(documents, cmd.getUserDetails());
    }

    @CommandHandler
    public void submitProposal(SubmitILProposalCommand cmd) {
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate.submitProposal(DateTime.now(), cmd.getUserDetails(), cmd.getComment(), proposalFinder);
    }

    @CommandHandler
    public void proposalApproval(ILProposalApprovalCommand cmd) {
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate.submitApproval(DateTime.now(), cmd.getUserDetails(), cmd.getComment(), cmd.getStatus());
    }

    @CommandHandler
    public void routeToNextLevel(ILProposalUnderwriterNextLevelCommand cmd) {
        ProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate.routeToNextLevel(cmd.getUserDetails(), cmd.getComment(), cmd.getStatus());
    }


}
