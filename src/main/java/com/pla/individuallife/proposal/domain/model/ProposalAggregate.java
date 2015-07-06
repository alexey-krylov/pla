package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.identifier.QuestionId;
import com.pla.individuallife.proposal.presentation.dto.AgentDetailDto;
import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import com.pla.sharedkernel.identifier.ProposalId;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalProcessorRole;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Document(collection = "individual_life_proposal")
public class ProposalAggregate extends AbstractAnnotatedAggregateRoot<ProposalId> {

    private final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    private ProposalSpecification specification=new ProposalSpecification();
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private Set<RiderDetail> riders;
    private String proposalNumber;
    @Id
    @AggregateIdentifier
    private ProposalId proposalId;
    private ProposalPlanDetail proposalPlanDetail;
    private List<Beneficiary> beneficiaries;
    private BigDecimal totalBeneficiaryShare = BigDecimal.ZERO;
    private List<Question> compulsoryHealthStatement;
    private GeneralDetails generalDetails;
    private FamilyPersonalDetail familyPersonalDetail;
    private AgentCommissionShareModel agentCommissionShareModel;
    private AdditionalDetails additionaldetails;
    private PremiumPaymentDetails premiumPaymentDetails;

    private ILProposalStatus proposalStatus;

    ProposalAggregate() {
        riders = new HashSet<RiderDetail>();
        beneficiaries = new ArrayList<Beneficiary>();
        agentCommissionShareModel = new AgentCommissionShareModel();
    }

    public ProposalAggregate(UserDetails userDetails, ProposalId proposalId, String proposalNumber, ProposedAssured proposedAssured, Set<AgentDetailDto> agentCommissionDetails) {
        riders = new HashSet<RiderDetail>();
        checkAuthorization(userDetails);
        this.proposalNumber = proposalNumber;
        this.proposalId = proposalId;
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        }
        assignProposedAssured(proposedAssured);
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        assignAgents(agentCommissionShareModel);
        this.proposalStatus = ILProposalStatus.DRAFT;
    }

    public void updateWithProposer(ProposalAggregate aggregate, Proposer proposer, UserDetails userDetails) {
        checkAuthorization(userDetails);
        assignProposer(proposer);
    }

    public void updatePlan(ProposalAggregate aggregate, ProposalPlanDetail proposalPlanDetail, Set<Beneficiary> beneficiaries, UserDetails userDetails) {
        checkAuthorization(userDetails);
        assignPlanDetail(proposalPlanDetail);
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

    private void assignPlanDetail(ProposalPlanDetail proposalPlanDetail) {
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
        Preconditions.checkArgument(newTotal.compareTo(PERCENTAGE) == -1, "Total share exceeds 100%. Cannot add any more beneficiary.");
        boolean sameBeneficiaryExists = beneficiaries.parallelStream().anyMatch(each -> (each.equals(beneficiary)));
        Preconditions.checkArgument(!sameBeneficiaryExists, "Beneficiary already exists.");
        beneficiaries.add(beneficiary);
        totalBeneficiaryShare = newTotal;
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


    public void updateAdditionalDetails(UserDetails userDetails, String medicalAttendantDetails, String medicalAttendantDuration, String dateAndReason, QuestionDto replacementDetails) {
        checkAuthorization(userDetails);
        this.additionaldetails = new AdditionalDetails(medicalAttendantDetails, medicalAttendantDuration, dateAndReason, new Question(new QuestionId(replacementDetails.getQuestionId()), replacementDetails.isAnswer(), replacementDetails.getAnswerResponse()));
    }

    public void updateWithPremiumPaymentDetail(ProposalAggregate aggregate, PremiumPaymentDetails premiumPaymentDetails, UserDetails userDetails) {
        checkAuthorization(userDetails);
        this.premiumPaymentDetails = premiumPaymentDetails;
    }
}
