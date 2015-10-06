package com.pla.individuallife.policy.domain.model;

import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.event.GHProposerAddedEvent;
import com.pla.sharedkernel.identifier.PolicyId;
import lombok.Getter;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 8/3/2015.
 */
@Document(collection = "individual_life_policy")
@Getter
public class IndividualLifePolicy extends AbstractAggregateRoot<PolicyId> {

    private final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    @Id
    @AggregateIdentifier
    private PolicyId  policyId;
    private Proposal proposal;
    private PolicyNumber policyNumber;
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private ProposalPlanDetail proposalPlanDetail;
    private List<Beneficiary> beneficiaries;
    private BigDecimal totalBeneficiaryShare = BigDecimal.ZERO;
    private List<Question> compulsoryHealthStatement;
    private GeneralDetails generalDetails;
    private FamilyPersonalDetail familyPersonalDetail;
    private AgentCommissionShareModel agentCommissionShareModel;
    private AdditionalDetails additionalDetails;
    private PremiumPaymentDetails premiumPaymentDetails;
    private List<ILProposerDocument> proposalDocuments;
    private PolicyStatus policyStatus;
    private DateTime inceptionOn;
    private DateTime expiredOn;


    public IndividualLifePolicy(PolicyId policyId, PolicyNumber policyNumber, Proposal proposal, DateTime inceptionOn, DateTime expiredOn) {
        checkArgument(policyId != null, "Policy ID cannot be empty");
        checkArgument(policyNumber != null, "Policy number cannot be empty");
        checkArgument(proposal != null, "Proposal cannot be empty");
        checkArgument(inceptionOn != null, "Policy inception date cannot be empty");
        checkArgument(expiredOn != null, "Policy expired date cannot be empty");
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.inceptionOn = inceptionOn;
        this.expiredOn = expiredOn;
        this.proposal = proposal;
        this.policyStatus = PolicyStatus.IN_FORCE;
//        ResidentialAddress proposerContactDetail = proposer.getResidentialAddress();
       /* if (this.proposer != null && this.proposer.getResidentialAddress() != null) {
            registerEvent(new GHProposerAddedEvent(proposer.getFirstName(), proposer.getTitle(),
                    proposerContactDetail.getAddress().getAddress1(), proposerContactDetail.getAddress().getAddress2(), proposerContactDetail.getAddress().getPostalCode(),
                    proposerContactDetail.getAddress().getProvince(), proposerContactDetail.getAddress().getTown(), proposerContactDetail.getEmailAddress()));
        }*/
    }

    public static IndividualLifePolicy createPolicy(PolicyId policyId, PolicyNumber policyNumber, Proposal proposal, DateTime policyInceptionDate, DateTime policyExpireDate) {
        return new IndividualLifePolicy(policyId,policyNumber,proposal,policyInceptionDate,policyExpireDate);
    }

    public IndividualLifePolicy withProposedAssured(ProposedAssured proposedAssured){
        this.proposedAssured  = proposedAssured;
        return this;
    }

    public IndividualLifePolicy withProposer(Proposer proposer){
        this.proposer  = proposer;
        return this;
    }

    public IndividualLifePolicy withProposalPlanDetail(ProposalPlanDetail proposalPlanDetail){
        this.proposalPlanDetail = proposalPlanDetail ;
        return this;
    }

    public IndividualLifePolicy withBeneficiaries(List<Beneficiary> beneficiaries){
        this.beneficiaries = beneficiaries;
        return this;
    }

    public IndividualLifePolicy withCompulsoryHealthStatement(List<Question> compulsoryHealthStatement){
        this.compulsoryHealthStatement = compulsoryHealthStatement;
        return this;
    }

    public IndividualLifePolicy withGeneralDetails(GeneralDetails generalDetails){
        this.generalDetails = generalDetails;
        return this;
    }

    public IndividualLifePolicy withFamilyPersonalDetail(FamilyPersonalDetail familyPersonalDetail){
        this.familyPersonalDetail = familyPersonalDetail;
        return this;
    }

    public IndividualLifePolicy withAgentCommissionShareModel(AgentCommissionShareModel agentCommissionShareModel){
        this.agentCommissionShareModel = agentCommissionShareModel;
        return this;
    }

    public IndividualLifePolicy withAdditionalDetails(AdditionalDetails additionaldetails){
        this.additionalDetails = additionaldetails;
        return this;
    }

    public IndividualLifePolicy withPremiumPaymentDetails(PremiumPaymentDetails premiumPaymentDetails){
        this.premiumPaymentDetails = premiumPaymentDetails;
        return this;
    }

    public IndividualLifePolicy withProposalDocuments(List<ILProposerDocument> proposalDocuments){
        this.proposalDocuments = proposalDocuments;
        return this;
    }


    @Override
    public PolicyId getIdentifier() {
        return policyId;
    }
}

