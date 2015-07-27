package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.identifier.QuestionId;
import com.pla.individuallife.proposal.presentation.dto.AgentDetailDto;
import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.quotation.presentation.dto.PlanDetailDto;
import com.pla.individuallife.quotation.presentation.dto.ProposerDto;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.sharedresource.event.ILQuotationConvertedToProposalEvent;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalApproverRole;
import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalProcessorRole;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Document(collection = "individual_life_proposal")
@Getter
public class ProposalAggregate extends AbstractAnnotatedAggregateRoot<ProposalId> {

    private final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    private ProposalSpecification specification=new ProposalSpecification();
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private String proposalNumber;
    @Id
    @AggregateIdentifier
    private ProposalId proposalId;
    private Quotation quotation;
    private ProposalPlanDetail proposalPlanDetail;
    private List<Beneficiary> beneficiaries;
    private BigDecimal totalBeneficiaryShare = BigDecimal.ZERO;
    private List<Question> compulsoryHealthStatement;
    private GeneralDetails generalDetails;
    private FamilyPersonalDetail familyPersonalDetail;
    private AgentCommissionShareModel agentCommissionShareModel;
    private AdditionalDetails additionaldetails;
    private PremiumPaymentDetails premiumPaymentDetails;
    private Set<ILProposerDocument> proposalDocuments;

    private ILProposalStatus proposalStatus;
    private DateTime submittedOn;
    private RoutingLevel routinglevel;


    ProposalAggregate() {
        beneficiaries = new ArrayList<Beneficiary>();
        agentCommissionShareModel = new AgentCommissionShareModel();
    }

    public ProposalAggregate(UserDetails userDetails, String proposalId, String proposalNumber, ProposedAssured proposedAssured, Set<AgentDetailDto> agentCommissionDetails) {
        checkAuthorization(userDetails);
        this.proposalNumber = proposalNumber;
        this.proposalId = new ProposalId(proposalId);
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        }
        assignProposedAssured(proposedAssured);
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        assignAgents(agentCommissionShareModel);
        this.proposalStatus = ILProposalStatus.DRAFT;
    }

    //TODO : When the proposal goes to SUBMITTED status the remaining versions of the same quotation has to be declined
    // and the quotation which is being converted to be changed with status CONVERTED when the proposal goes to state SUBMITTED
    public ProposalAggregate(UserDetails userDetails, String proposalId, String proposalNumber, ProposedAssured proposedAssured, Set<AgentDetailDto> agentCommissionDetails, ILQuotationDto quotationDto, PlanFinder planFinder) {
        checkAuthorization(userDetails);
        this.proposalNumber = proposalNumber;
        this.proposalId = new ProposalId(proposalId);
        assignProposedAssured(proposedAssured);
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        assignAgents(agentCommissionShareModel);
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        } else {
            assignProposer(quotationDto.getProposer());
        }

        assignPlan(quotationDto.getPlanDetailDto(), planFinder);

        this.proposalStatus = ILProposalStatus.DRAFT;
        this.quotation = new Quotation(quotationDto.getQuotationNumber(), quotationDto.getVersionNumber(),quotationDto.getQuotationId().toString());

    }

    private void assignPlan(PlanDetailDto dto, PlanFinder planFinder) {
        ProposalPlanDetail planDetail = new ProposalPlanDetail();
        try {
            BeanUtils.copyProperties(planDetail, dto);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        assignPlanDetail(planDetail, planFinder);
    }

    private void assignProposer(ProposerDto dto) {
        Proposer proposer = new Proposer();
        proposer.setTitle(dto.getTitle());
        proposer.setFirstName(dto.getFirstName());
        proposer.setSurname(dto.getSurname());
        proposer.setDateOfBirth(dto.getDateOfBirth());
        proposer.setGender(dto.getGender());
        proposer.setMobileNumber(dto.getMobileNumber());
        proposer.setEmailAddress(dto.getEmailAddress());
        this.proposer = proposer;
    }

    public void updateWithProposer(ProposalAggregate aggregate, Proposer proposer, UserDetails userDetails) {
        checkAuthorization(userDetails);
        assignProposer(proposer);
    }

    public void updatePlan(ProposalAggregate aggregate, ProposalPlanDetail proposalPlanDetail, Set<Beneficiary> beneficiaries, UserDetails userDetails, PlanFinder planFinder) {
        checkAuthorization(userDetails);
        assignPlanDetail(proposalPlanDetail, planFinder);
        assignBeneficiaries(beneficiaries);
    }

    private void checkAuthorization(UserDetails userDetails){
        boolean hasProposalPreprocessorRole = hasIndividualLifeProposalProcessorRole(userDetails.getAuthorities());
        if (!hasProposalPreprocessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal processor(ROLE_PROPOSAL_PROCESSOR) authority");
        }
        Preconditions.checkArgument(hasProposalPreprocessorRole);
    }

    private void assignBeneficiaries(Set<Beneficiary> beneficiaries) {
        this.beneficiaries = new ArrayList<Beneficiary>();
        this.totalBeneficiaryShare = BigDecimal.ZERO;
        beneficiaries.forEach(beneficiary -> this.addBeneficiary(beneficiary));
    }

    private void assignPlanDetail(ProposalPlanDetail proposalPlanDetail, PlanFinder planFinder) {
        specification.checkProposerAgainstPlan(proposalPlanDetail, planFinder, this.proposedAssured);
        this.proposalPlanDetail = proposalPlanDetail;
    }

    private void assignProposer(ProposedAssured proposedAssured) {
        specification.checkProposedAssured(proposedAssured);
        try {
            Proposer proposer = new Proposer();
            BeanUtils.copyProperties(proposer, proposedAssured);
            assignProposer(proposer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



    private void assignProposedAssured(ProposedAssured proposedAssured) {
        specification.checkProposedAssured(proposedAssured);
        this.proposedAssured = proposedAssured;
    }

    private void assignProposer(Proposer proposer) {
        specification.checkProposer(proposer);
        this.proposer = proposer;
    }

    private void assignPlan(ProposalPlanDetail proposalPlanDetail) {
        this.proposalPlanDetail = proposalPlanDetail;
    }

    private void assignAgents(AgentCommissionShareModel agentCommissionModel) {
        this.agentCommissionShareModel = agentCommissionModel;
    }

    public void addBeneficiary(Beneficiary beneficiary) {
        BigDecimal newTotal = totalBeneficiaryShare.add(beneficiary.getShare());
        Preconditions.checkArgument(newTotal.compareTo(PERCENTAGE) <= 0, "Total share exceeds 100%. Cannot add any more beneficiary.");
        boolean sameBeneficiaryExists = beneficiaries.parallelStream().anyMatch(each -> (each.equals(beneficiary)));
        Preconditions.checkArgument(!sameBeneficiaryExists, "Beneficiary already exists.");
        beneficiaries.add(beneficiary);
        totalBeneficiaryShare = newTotal;
    }

    public void updateWithProposedAssuredAndAgenDetails(UserDetails userDetails, String proposalId, ProposedAssured proposedAssured, Set<AgentDetailDto> agentCommissionDetails) {
        checkAuthorization(userDetails);
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        }
        assignProposedAssured(proposedAssured);
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        assignAgents(agentCommissionShareModel);
    }

    public void updateGeneralDetails(GeneralDetails generaldetails, UserDetails userDetails) {
        checkAuthorization(userDetails);
        this.generalDetails = generaldetails;
    }

    public void updateCompulsoryHealthStatement(List<QuestionDto> compulsoryHealthStatement, UserDetails userDetails){
        checkAuthorization(userDetails);
        this.compulsoryHealthStatement = new ArrayList<Question>();
        compulsoryHealthStatement.stream().forEach(ch -> this.compulsoryHealthStatement.add(new Question(new QuestionId(ch.getQuestionId()), ch.isAnswer(), ch.getAnswerResponse())));
    }


    public void updateFamilyPersonalDetail(FamilyPersonalDetail personalDetail, UserDetails userDetails){
        checkAuthorization(userDetails);
        this.familyPersonalDetail=personalDetail;
    }

    public void updateAdditionalDetails(UserDetails userDetails, String medicalAttendantDetails, String medicalAttendantDuration, String dateAndReason, ReplacementQuestion replacementDetails) {
        checkAuthorization(userDetails);
        this.additionaldetails = new AdditionalDetails(medicalAttendantDetails, medicalAttendantDuration, dateAndReason, new ReplacementQuestion(replacementDetails.getQuestionId(), replacementDetails.isAnswer(), replacementDetails.getAnswerResponse1(), replacementDetails.getAnswerResponse2()));
    }

    public void updateWithPremiumPaymentDetail(ProposalAggregate aggregate, PremiumPaymentDetails premiumPaymentDetails, UserDetails userDetails) {
        checkAuthorization(userDetails);
        this.premiumPaymentDetails = premiumPaymentDetails;
    }

    // TODO WIll additional documents also be followed up
    public void updateWithDocuments(Set<ILProposerDocument> proposalDocuments, UserDetails userDetails) {
        checkAuthorization(userDetails);
        this.proposalDocuments = proposalDocuments;
        // raise event to store document in client BC
    }

    public void submitProposal(DateTime submittedOn, UserDetails userDetails, String comment, ILProposalFinder proposalFinder) {
        checkAuthorization(userDetails);
        this.submittedOn = submittedOn;
        this.proposalStatus = ILProposalStatus.PENDING_ACCEPTANCE;
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(proposalPlanDetail.getPlanId()), LocalDate.now(), ProcessType.ENROLLMENT.name());

        RoutingLevel routinglevel = proposalFinder.findRoutingLevel(routingLevelDetailDto, proposalId.toString(), (Integer) proposedAssured.getAgeNextBirthday());
        this.routinglevel = routinglevel;
        if (this.quotation != null)
            registerEvent(new ILQuotationConvertedToProposalEvent(this.quotation.getQuotationNumber(), new QuotationId(this.quotation.getQuotationId())));
    }

    public void submitApproval(DateTime now, UserDetails userDetails, String comment, ILProposalStatus status) {
        boolean hasProposalApprovalRole = hasIndividualLifeProposalApproverRole(userDetails.getAuthorities());
        if (!hasProposalApprovalRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal Approval (INDIVIDUAL_LIFE_PROPOSAL_APPROVER) authority");
        }
        Preconditions.checkArgument(hasProposalApprovalRole);
        this.proposalStatus = status;
        if (ILProposalStatus.APPROVED.equals(status)) {
            DateTime approvedOn = DateTime.now();
            markASFirstPremiumPending(userDetails.getUsername(), DateTime.now(), comment);
            markASINForce(userDetails.getUsername(), approvedOn, comment);
        }
    }

    public void routeToNextLevel(UserDetails userDetails, String comment, ILProposalStatus status, RoutingLevel routingLevel) {
        boolean hasProposalApprovalRole = hasIndividualLifeProposalApproverRole(userDetails.getAuthorities());
        if (!hasProposalApprovalRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal Approval (INDIVIDUAL_LIFE_PROPOSAL_APPROVER) authority");
        }
        Preconditions.checkArgument(hasProposalApprovalRole);
        RoutingLevel nextRoutingLevel = routingLevel.getNext();
        Preconditions.checkNotNull(nextRoutingLevel);
        this.routinglevel = nextRoutingLevel;
        this.proposalStatus = status;
    }

    public void markASFirstPremiumPending(String approvedBy, DateTime approvedOn, String comment) {
        this.proposalStatus = ILProposalStatus.PENDING_FIRST_PREMIUM;
    }

    public void markASINForce(String approvedBy, DateTime approvedOn, String comment) {
        this.proposalStatus = ILProposalStatus.IN_FORCE;
        //TODO : need to register to ILProposalToPolicyEvent so that proposal becomes the policy
        //registerEvent(new ILProposalToPolicyEvent(this.proposalId));
    }
}
