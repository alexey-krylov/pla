package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Preconditions;
import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import com.pla.individuallife.proposal.domain.event.ILProposalStatusAuditEvent;
import com.pla.individuallife.proposal.domain.event.ILProposalSubmitEvent;
import com.pla.individuallife.sharedresource.event.ILProposalToPolicyEvent;
import com.pla.individuallife.sharedresource.event.ILQuotationConvertedToProposalEvent;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Document(collection = "individual_life_proposal")
@Getter
public class ILProposalAggregate extends AbstractAnnotatedAggregateRoot<ProposalId> {

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

    ILProposalAggregate() {
        beneficiaries = new ArrayList<Beneficiary>();
        agentCommissionShareModel = new AgentCommissionShareModel();
    }

    public ILProposalAggregate(String proposalId, String proposalNumber, ProposedAssured proposedAssured, AgentCommissionShareModel agentCommissionShareModel) {
        this.proposalNumber = proposalNumber;
        this.proposalId = new ProposalId(proposalId);
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        }
        assignProposedAssured(proposedAssured);
        this.agentCommissionShareModel = agentCommissionShareModel;
        this.proposalStatus = ILProposalStatus.DRAFT;
    }



    public ILProposalAggregate(String proposalId, String proposalNumber, ProposedAssured proposedAssured, AgentCommissionShareModel agentCommissionShareModel, Proposer proposer, String quotationNumber, int versionNumber, String quotationId, ProposalPlanDetail proposalPlanDetail, int minAge, int maxAge) {
        this.proposalNumber = proposalNumber;
        this.proposalId = new ProposalId(proposalId);
        assignProposedAssured(proposedAssured);
        assignAgents(agentCommissionShareModel);
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        } else {
            assignProposer(proposer);
        }
        specification.checkProposerAgainstPlan(minAge,maxAge,proposedAssured.getAgeNextBirthday());
        this.proposalPlanDetail = proposalPlanDetail;
        this.proposalStatus = ILProposalStatus.DRAFT;
        this.quotation = new Quotation(quotationNumber, versionNumber,quotationId.toString());
    }

    public ILProposalAggregate updateWithProposer(Proposer proposer) {
        assignProposer(proposer);
        return this;
    }

    public ILProposalAggregate updatePlan(ProposalPlanDetail proposalPlanDetail, Set<Beneficiary> beneficiaries,int minAge,int maxAge) {
        specification.checkProposerAgainstPlan(minAge,maxAge,proposedAssured.getAgeNextBirthday());
        this.proposalPlanDetail = proposalPlanDetail;
        assignBeneficiaries(beneficiaries);
        return this;
    }


    private void assignBeneficiaries(Set<Beneficiary> beneficiaries) {
        this.beneficiaries = new ArrayList<Beneficiary>();
        this.totalBeneficiaryShare = BigDecimal.ZERO;
        beneficiaries.forEach(beneficiary -> this.addBeneficiary(beneficiary));
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

    public ILProposalAggregate updateWithProposedAssuredAndAgentDetails(ProposedAssured proposedAssured, AgentCommissionShareModel agentCommissionShareModel) {
        if(proposedAssured.getIsProposer()) {
            assignProposer(proposedAssured);
        }
        assignProposedAssured(proposedAssured);
        assignAgents(agentCommissionShareModel);
        return this;
    }

    public ILProposalAggregate updateGeneralDetails(GeneralDetails generaldetails) {
        this.generalDetails = generaldetails;
        return this;
    }

    public ILProposalAggregate updateCompulsoryHealthStatement(List<Question> compulsoryHealthStatement){
        this.compulsoryHealthStatement = compulsoryHealthStatement;
        return this;
    }


    public ILProposalAggregate updateFamilyPersonalDetail(FamilyPersonalDetail personalDetail){
        this.familyPersonalDetail=personalDetail;
        return this;
    }

    public ILProposalAggregate updateAdditionalDetails(String medicalAttendantDetails, String medicalAttendantDuration, String dateAndReason, ReplacementQuestion replacementDetails) {
        this.additionaldetails = new AdditionalDetails(medicalAttendantDetails, medicalAttendantDuration, dateAndReason, new ReplacementQuestion(replacementDetails.getQuestionId(), replacementDetails.isAnswer(), replacementDetails.getAnswerResponse1(), replacementDetails.getAnswerResponse2()));
        return this;
    }

    public ILProposalAggregate updateWithPremiumPaymentDetail(PremiumPaymentDetails premiumPaymentDetails) {
        this.premiumPaymentDetails = premiumPaymentDetails;
        return this;
    }

    // TODO WIll additional documents also be followed up
    public ILProposalAggregate updateWithDocuments(Set<ILProposerDocument> proposalDocuments) {
        this.proposalDocuments = proposalDocuments;
        return this;
    }

    public ILProposalAggregate submitProposal(DateTime submittedOn,String comment,RoutingLevel routinglevel) {
        this.submittedOn = submittedOn;
        this.proposalStatus = routinglevel!=null?RoutingLevel.UNDERWRITING_LEVEL_ONE.equals(routinglevel)?ILProposalStatus.URL1_PENDING_ACCEPTANCE:ILProposalStatus.URL2_PENDING_ACCEPTANCE:
                ILProposalStatus.PENDING_ACCEPTANCE;
        if (this.quotation != null)
            registerEvent(new ILQuotationConvertedToProposalEvent(this.quotation.getQuotationNumber(), new QuotationId(this.quotation.getQuotationId())));
        registerEvent(new ILProposalSubmitEvent(this.proposalId));
        return this;
    }

    public ILProposalAggregate submitApproval(DateTime now,  String comment, ILProposalStatus status,String userName) {
        this.proposalStatus = status;
        if (ILProposalStatus.APPROVED.equals(status)) {
            DateTime approvedOn = now;
            markASFirstPremiumPending(userName, DateTime.now(), comment);
            markASINForce(userName, approvedOn, comment);
        }
        return this;
    }

    /*
    * store the comment when routing to  next level
    * */
    public ILProposalAggregate routeToNextLevel(String comment, ILProposalStatus status) {
        this.proposalStatus = ILProposalStatus.URL1_PENDING_ACCEPTANCE.equals(status)?ILProposalStatus.URL2_PENDING_ACCEPTANCE:status;
        return this;
    }

    public ILProposalAggregate markASFirstPremiumPending(String approvedBy, DateTime approvedOn, String comment) {
        this.proposalStatus = ILProposalStatus.PENDING_FIRST_PREMIUM;
        registerEvent(new ILProposalStatusAuditEvent(this.getProposalId(), ProposalStatus.PENDING_FIRST_PREMIUM, approvedBy, comment, approvedOn));
        return this;
    }

    public ILProposalAggregate markASINForce(String approvedBy, DateTime approvedOn, String comment) {
        this.proposalStatus = ILProposalStatus.IN_FORCE;
        registerEvent(new ILProposalStatusAuditEvent(this.getProposalId(), ProposalStatus.IN_FORCE, approvedBy, comment, approvedOn));
        registerEvent(new ILProposalToPolicyEvent(this.proposalId));
        return this;
    }

    /*
    * Check if status should be declined when closing the Proposal
    * */
    public void closeProposal() {
        this.proposalStatus = ILProposalStatus.DECLINED;
    }

    @Override
    public ProposalId getIdentifier() {
        return proposalId;
    }
}
