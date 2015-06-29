package com.pla.grouplife.proposal.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Samir on 6/24/2015.
 */
@Document(collection = "group_life_proposal")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifeProposal extends AbstractAggregateRoot<ProposalId> {

    @Id
    @AggregateIdentifier
    private ProposalId proposalId;

    private QuotationId quotationId;

    private String proposalNumber;

    private DateTime submittedOn;

    private AgentId agentId;

    private Proposer proposer;

    private Set<Insured> insureds;

    private PremiumDetail premiumDetail;

    private GLProposalStatus proposalStatus;

    private List<ProposerDocument> proposerDocuments;

    @Override
    public ProposalId getIdentifier() {
        return proposalId;
    }

    public GroupLifeProposal(ProposalId proposalId, QuotationId quotationId, String proposalNumber,AgentId agentId,Proposer glProposer
    ,Set<Insured> insureds, PremiumDetail premiumDetail,GLProposalStatus glProposalStatus) {
        checkArgument(proposalId != null, "Proposal ID cannot be blank");
        checkArgument(quotationId != null, "Quotation ID cannot be blank");
        checkArgument(agentId != null, "Agent ID cannot be blank");
        checkArgument(proposalNumber != null, "Proposal Number cannot be blank");
        checkArgument(glProposer != null, "Proposer cannot be blank");
       // checkArgument(insureds != null, "Insured Details cannot be blank");
       // checkArgument(premiumDetail != null, "Premium Details cannot be blank");
       // checkArgument(glProposalStatus != null, "Proposal Status cannot be blank");
        this.proposalId = proposalId;
        this.quotationId = quotationId;
        this.proposalNumber = proposalNumber;
        this.agentId = agentId;
        this.proposer = glProposer;
        this.insureds = insureds;
        this.premiumDetail = premiumDetail;
        this.proposalStatus = glProposalStatus;
    }

    public static GroupLifeProposal createGroupLifeProposal(ProposalId proposalId, QuotationId quotationId, String proposalNumber,AgentId agentId,Proposer glProposer
            ,Set<Insured> insureds, PremiumDetail premiumDetail,GLProposalStatus glProposalStatus){
        return new GroupLifeProposal(proposalId,quotationId,proposalNumber,agentId,glProposer,insureds,premiumDetail,glProposalStatus);
    }

    public GroupLifeProposal updateWithAgentId(AgentId agentId) {
        this.agentId = agentId;
        return this;
    }

    public GroupLifeProposal updateWithProposer(Proposer proposer) {
        this.proposer = proposer;
        return this;
    }

    public GroupLifeProposal updateWithInsureds(Set<Insured> insureds) {
        this.insureds = insureds;
        return this;
    }

    public GroupLifeProposal updateWithPremiumDetail(PremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }

    //TODO Document followup should be scheduled after submitting the proposal or even before also?
    // TODO WIll additional documents also be followed up
    public GroupLifeProposal updateWithDocuments(List<ProposerDocument> proposerDocuments) {
        this.proposerDocuments = proposerDocuments;
        // raise event to store document in client BC
        return this;
    }

    public GroupLifeProposal submitForApproval(DateTime submittedOn) {
        this.submittedOn = submittedOn;
        this.proposalStatus = GLProposalStatus.SUBMITTED;
        //raise Event
        return this;
    }

    private GroupLifeProposal approvedByAprover(String approvedBy) {
        this.proposalStatus = GLProposalStatus.APPROVED;
        //raise event
        return this;
    }

    private GroupLifeProposal markASFirstPremiumPending() {
        this.proposalStatus = GLProposalStatus.PENDING_FIRST_PREMIUM;
        //raise event
        return this;
    }

    private GroupLifeProposal returnedByApprover(String returnedBy) {
        this.proposalStatus = GLProposalStatus.RETURNED;
        // raise event
        return this;
    }

    /*public BigDecimal getNetAnnualPremiumPaymentAmount(PremiumDetail premiumDetail) {
        BigDecimal totalInsuredPremiumAmount = this.getTotalBasicPremiumForInsured();
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add(addOnBenefitAmount);
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add(profitAndSolvencyAmount);
        BigDecimal waiverOfExcessLoading = premiumDetail.getWaiverOfExcessLoading() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getWaiverOfExcessLoading().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add(waiverOfExcessLoading);
        BigDecimal discountAmount = premiumDetail.getDiscount() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getDiscount().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.subtract(discountAmount);
        BigDecimal vat = premiumDetail.getVat() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getVat().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add(vat);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }*/

    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }
}
