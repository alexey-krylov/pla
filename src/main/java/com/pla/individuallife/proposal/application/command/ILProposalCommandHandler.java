package com.pla.individuallife.proposal.application.command;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.identifier.QuestionId;
import com.pla.individuallife.proposal.domain.model.ILProposalAggregate;
import com.pla.individuallife.proposal.domain.model.ILProposalApprover;
import com.pla.individuallife.proposal.domain.model.ILProposalProcessor;
import com.pla.individuallife.proposal.domain.model.ILProposalStatus;
import com.pla.individuallife.proposal.domain.service.ILProposalRoleAdapter;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.proposal.service.ILProposalFactory;
import com.pla.individuallife.proposal.service.ILProposalService;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.individuallife.proposal.exception.ILProposalException.raiseMandatoryDocumentNotUploaded;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Prasant on 26-May-15.
 */
@Component
public class ILProposalCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(ILProposalCommandHandler.class);
    @Autowired
    private ProposalNumberGenerator proposalNumberGenerator;
    @Autowired
    private Repository<ILProposalAggregate> ilProposalMongoRepository;

    @Autowired
    private PlanFinder planFinder;

    @Autowired
    private ILProposalFinder proposalFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ILProposalRoleAdapter ilProposalRoleAdapter;

    @Autowired
    private ILProposalFactory ilProposalFactory;

    @Autowired
    private ILProposalService ilProposalService;

    /*
    *
    * Generate a Proposal Number once the Proposed Assured details has been submitted
    * */
    @CommandHandler
    public String createProposal(ILCreateProposalCommand cmd) {
        ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ILProposalAggregate proposalAggregate = ilProposalFactory.createProposal(cmd);
        ilProposalMongoRepository.add(proposalAggregate);
        return proposalAggregate.getIdentifier().getProposalId();
    }

    /*
    *
    * Update the IL Proposal  with Proposed Assured Details
    * */
    @CommandHandler
    public String updateProposalProposedAssured(ILUpdateProposalWithProposedAssuredCommand cmd) {
        ILProposalProcessor ilProposalProcessor  = ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ProposedAssured proposedAssured = ProposedAssuredBuilder.getProposedAssuredBuilder(cmd.getProposedAssured()).createProposedAssured();
        if (logger.isDebugEnabled()) {
            logger.debug(" ProposedAssured :: " + proposedAssured);
        }
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate = ilProposalProcessor.updateWithProposedAssuredAndAgentDetails(aggregate,proposedAssured);
        ilProposalMongoRepository.add(aggregate);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateProposalProposer(ILProposalUpdateWithProposerCommand cmd) {
        ILProposalProcessor ilProposalProcessor  =  ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        Proposer proposer = ProposerBuilder.getProposerBuilder(cmd.getProposer()).createProposer();
        AgentCommissionShareModel agentCommissionShareModel  = withAgentCommissionShareModel(cmd.getAgentCommissionDetails());
        if (logger.isDebugEnabled()) {
            logger.debug(" Proposer :: " + proposer);
        }
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        if (!aggregate.getProposalPlanDetail().getPlanId().equals(cmd.getPlanDetail().getPlanId())){
            cmd.getPlanDetail().setRiderDetails(null);
        }
        aggregate = ilProposalProcessor.updateWithProposer(aggregate,proposer,agentCommissionShareModel,cmd.getPlanDetail());
        ilProposalMongoRepository.add(aggregate);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateGeneralDetails(ILProposalUpdateGeneralDetailsCommand cmd) {
        ILProposalProcessor ilProposalProcessor  = ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        GeneralDetails generalDetails = new GeneralDetails(cmd.getAssuredByPLAL(),cmd.getPendingInsuranceByOthers(),cmd.getAssuranceDeclined(),cmd.getAssuredByOthers(),cmd.getGeneralQuestion());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(proposalId);
        aggregate = ilProposalProcessor.updateGeneralDetails(aggregate,generalDetails);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateAdditionalDetails(ILProposalUpdateAdditionalDetailsCommand cmd) {
        ILProposalProcessor ilProposalProcessor  =   ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ILProposalAggregate proposalAggregate = null;
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        proposalAggregate = ilProposalMongoRepository.load(proposalId);
        proposalAggregate =  ilProposalProcessor.updateAdditionalDetails(proposalAggregate,cmd.getMedicalAttendantDetails(), cmd.getMedicalAttendantDuration(), cmd.getDateAndReason(), cmd.getReplacementDetails());
        return proposalAggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateCompulsoryHealthStatement(ILProposalUpdateCompulsoryHealthStatementCommand cmd) {
        ILProposalProcessor ilProposalProcessor  = ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        List<QuestionDto> questionDtoList=cmd.getCompulsoryHealthDetails();
        ILProposalAggregate proposalAggregate = ilProposalMongoRepository.load(proposalId);
        List<Question> quotations  = withCompulsoryHealthStatement(questionDtoList);
        proposalAggregate = ilProposalProcessor.updateCompulsoryHealthStatement(proposalAggregate,quotations);
        return proposalAggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateFamilyPersonalDetails(ILProposalUpdateFamilyPersonalDetailsCommand cmd) {
        ILProposalProcessor ilProposalProcessor  = ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        FamilyPersonalDetail familyPersonalDetail = cmd.getFamilyPersonalDetail();
        ILProposalAggregate proposalAggregate = ilProposalMongoRepository.load(proposalId);
        proposalAggregate = ilProposalProcessor.updateFamilyPersonalDetail(proposalAggregate,familyPersonalDetail);
        return proposalAggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithPlanDetail(ILProposalUpdateWithPlanAndBeneficiariesCommand cmd) throws InvocationTargetException, IllegalAccessException {
        ILProposalProcessor ilProposalProcessor   =  ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(proposalId);
        Map plan = planFinder.findPlanByPlanId(new PlanId(cmd.getProposalPlanDetail().getPlanId()));
        Map planDetail = (HashMap) plan.get("planDetail");
        int minAge = (int) planDetail.get("minEntryAge");
        int maxAge = (int) planDetail.get("maxEntryAge");
        aggregate = ilProposalProcessor.updateWithPlanDetail(aggregate, cmd.getProposalPlanDetail(), cmd.getBeneficiaries(), minAge, maxAge);
        ilProposalMongoRepository.add(aggregate);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithPremiumPaymentDetail(ILProposalUpdatePremiumPaymentDetailsCommand cmd) {
        ILProposalProcessor ilProposalProcessor  = ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ProposalId proposalId = new ProposalId(cmd.getProposalId());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(proposalId);
        PremiumPaymentDetails premiumPaymentDetails = cmd.getPremiumPaymentDetails();
        aggregate = ilProposalProcessor.updateWithPremiumPaymentDetail(aggregate,premiumPaymentDetails);
        ilProposalMongoRepository.add(aggregate);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String uploadMandatoryDocument(ILProposalDocumentCommand cmd) throws IOException {
        String fileName = cmd.getFile() != null ? cmd.getFile().getOriginalFilename() : "";
        ILProposalProcessor ilProposalProcessor  =  ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        Set<ILProposerDocument> documents = aggregate.getProposalDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(cmd.getFile().getInputStream(),fileName,cmd.getFile().getContentType()).getId().toString();
        ILProposerDocument currentDocument = new ILProposerDocument(cmd.getDocumentId(), cmd.getFilename(), gridFsDocId, cmd.getFile().getContentType(),cmd.isMandatory(),true);
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
        aggregate = ilProposalProcessor.updateWithDocuments(aggregate,documents);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String submitProposal(SubmitILProposalCommand cmd) {
        ILProposalProcessor ilProposalProcessor = ilProposalRoleAdapter.userToProposalProcessorRole(cmd.getUserDetails());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(aggregate.getProposalPlanDetail().getPlanId()), LocalDate.now(), ProcessType.ENROLLMENT.name());
        RoutingLevel routinglevel = ilProposalService.findRoutingLevel(routingLevelDetailDto, aggregate.getProposalId().toString(), aggregate.getProposedAssured().getAgeNextBirthday());
        aggregate = ilProposalProcessor.submitProposal(aggregate,cmd.getUserDetails().getUsername(),cmd.getComment(), routinglevel);
        List<ILProposalMandatoryDocumentDto> glProposalMandatoryDocumentDtos = ilProposalService.findAllMandatoryDocument(cmd.getProposalId());
        Set<ILProposerDocument> proposerDocuments  = aggregate.getProposalDocuments()!=null?aggregate.getProposalDocuments():Sets.newLinkedHashSet();
        if (isNotEmpty(proposerDocuments)) {
            Set<String> documentIds = proposerDocuments.parallelStream().map(new Function<ILProposerDocument, String>() {
                @Override
                public String apply(ILProposerDocument glProposerDocument) {
                    return glProposerDocument.getDocumentId();
                }
            }).collect(Collectors.toSet());
            glProposalMandatoryDocumentDtos.parallelStream().filter(new Predicate<ILProposalMandatoryDocumentDto>() {
                @Override
                public boolean test(ILProposalMandatoryDocumentDto glProposalMandatoryDocumentDto) {
                    return !documentIds.contains(glProposalMandatoryDocumentDto.getDocumentId());
                }
            }).map(new Function<ILProposalMandatoryDocumentDto, ILProposerDocument>() {
                @Override
                public ILProposerDocument apply(ILProposalMandatoryDocumentDto glProposalMandatoryDocumentDto) {
                    proposerDocuments.add(new ILProposerDocument(glProposalMandatoryDocumentDto.getDocumentId(), true, false));
                    return null;
                }
            }).collect(Collectors.toSet());
            aggregate = aggregate.updateWithDocuments(proposerDocuments);
        }
        else {
            Set<ILProposerDocument> ilProposerDocuments = glProposalMandatoryDocumentDtos.parallelStream().map(new Function<ILProposalMandatoryDocumentDto, ILProposerDocument>() {
                @Override
                public ILProposerDocument apply(ILProposalMandatoryDocumentDto ilProposalMandatoryDocumentDto) {
                    return  new ILProposerDocument(ilProposalMandatoryDocumentDto.getDocumentId(), true, false);
                }
            }).collect(Collectors.toSet());
            aggregate = aggregate.updateWithDocuments(ilProposerDocuments);
        }
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String proposalApproval(ILProposalApprovalCommand cmd) {
        ILProposalApprover ilProposalApprover = ilProposalRoleAdapter.userToProposalApproverRole(cmd.getUserDetails());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        if (ILProposalStatus.APPROVED.equals(cmd.getStatus()) && !ilProposalService.doesAllDocumentWaivesByApprover(cmd.getProposalId())){
            raiseMandatoryDocumentNotUploaded();
        }
        aggregate = ilProposalApprover.submitApproval(aggregate,cmd.getComment(), cmd.getStatus(),cmd.getUserDetails().getUsername());
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String routeToNextLevel(ILProposalUnderwriterNextLevelCommand cmd) {
        ILProposalApprover ilProposalApprover =  ilProposalRoleAdapter.userToProposalApproverRole(cmd.getUserDetails());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        aggregate = ilProposalApprover.routeToNextLevel(aggregate,cmd.getComment(), cmd.getStatus());
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String waiveDocumentCommandHandler(WaiveMandatoryDocumentCommand cmd) {
        ILProposalApprover ilProposalApprover =  ilProposalRoleAdapter.userToProposalApproverRole(cmd.getUserDetails());
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(new ProposalId(cmd.getProposalId()));
        Set<ILProposerDocument> documents = aggregate.getProposalDocuments();
        Set<ILProposerDocument> ilProposerDocumentSet = Sets.newLinkedHashSet();
        if (isNotEmpty(cmd.getWaivedDocuments())) {
            List<String> waiveDocumentList = cmd.getWaivedDocuments().parallelStream().map(new Function<ILProposalMandatoryDocumentDto, String>() {
                @Override
                public String apply(ILProposalMandatoryDocumentDto ilProposalMandatoryDocumentDto) {
                    return ilProposalMandatoryDocumentDto.getDocumentId();
                }
            }).collect(Collectors.toList());
            documents.forEach(ilProposerDocument -> {
                if (waiveDocumentList.contains(ilProposerDocument.getDocumentId())) {
                    ilProposerDocument.setApproved(true);
                    ilProposerDocumentSet.add(ilProposerDocument);
                } else {
                    ilProposerDocumentSet.add(ilProposerDocument);
                }
            });
        }
        aggregate = ilProposalApprover.updateWithDocuments(aggregate,ilProposerDocumentSet);
        return aggregate.getIdentifier().getProposalId();
    }

    @CommandHandler
    public void closureILProposal(ILProposalClosureCommand cmd) {
        ILProposalAggregate aggregate = ilProposalMongoRepository.load(cmd.getProposalId());
        aggregate.closeProposal();
    }

    private List<Question> withCompulsoryHealthStatement(List<QuestionDto> compulsoryHealthStatement){
        return compulsoryHealthStatement.parallelStream().map(new Function<QuestionDto, Question>() {
            @Override
            public Question apply(QuestionDto questionDto) {
                return new Question(new QuestionId(questionDto.getQuestionId()), questionDto.isAnswer(), questionDto.getAnswerResponse());
            }
        }).collect(Collectors.toList());
    }

    private AgentCommissionShareModel withAgentCommissionShareModel(Set<AgentDetailDto> agentCommissionDetails){
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        return agentCommissionShareModel;
    }



}
