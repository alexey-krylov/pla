package com.pla.grouphealth.proposal.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.proposal.domain.event.GHProposalStatusAuditEvent;
import com.pla.grouphealth.sharedresource.event.GHQuotationConvertedToProposalEvent;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHPremiumDetail;
import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.sharedkernel.domain.model.Quotation;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
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
@Document(collection = "group_health_proposal")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupHealthProposal extends AbstractAggregateRoot<ProposalId> {

    @Id
    @AggregateIdentifier
    private ProposalId proposalId;

    private Quotation quotation;

    private ProposalNumber proposalNumber;

    private DateTime submittedOn;

    private AgentId agentId;

    private GHProposer proposer;

    private Set<GHInsured> insureds;

    private GHPremiumDetail premiumDetail;

    private ProposalStatus proposalStatus;

    private List<GHProposerDocument> proposerDocuments;

    private OpportunityId opportunityId;

    @Override
    public ProposalId getIdentifier() {
        return proposalId;
    }

    public GroupHealthProposal(ProposalId proposalId, Quotation quotation, ProposalNumber proposalNumber) {
        checkArgument(proposalId != null, "Proposal ID cannot be blank");
        checkArgument(quotation != null, "Quotation ID cannot be blank");
        checkArgument(proposalNumber != null, "Proposal Number cannot be blank");
        this.proposalId = proposalId;
        this.quotation = quotation;
        this.proposalNumber = proposalNumber;
        this.proposalStatus=ProposalStatus.DRAFT;
    }

    public GroupHealthProposal updateWithAgentId(AgentId agentId) {
        this.agentId = agentId;
        return this;
    }

    public GroupHealthProposal updateWithProposer(GHProposer proposer) {
        this.proposer = proposer;
        return this;
    }

    public GroupHealthProposal updateWithInsureds(Set<GHInsured> insureds) {
        this.insureds = insureds;
        return this;
    }

    public GroupHealthProposal updateWithPremiumDetail(GHPremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }

    //TODO Document followup should be scheduled after submitting the proposal or even before also?
    // TODO WIll additional documents also be followed up
    // TODO Answer: When Proposal becomes policy then  document reminder should go
    public GroupHealthProposal updateWithDocuments(List<GHProposerDocument> proposerDocuments) {
        this.proposerDocuments = proposerDocuments;
        // raise event to store document in client BC
        return this;
    }

    public GroupHealthProposal submitForApproval(DateTime submittedOn, String submittedBy, String comment) {
        this.submittedOn = submittedOn;
        this.proposalStatus = ProposalStatus.PENDING_ACCEPTANCE;
        registerEvent(new GHQuotationConvertedToProposalEvent(this.quotation.getQuotationNumber(), this.quotation.getQuotationId()));
        registerEvent(new GHProposalStatusAuditEvent(this.getProposalId(), ProposalStatus.PENDING_ACCEPTANCE, submittedBy, comment, submittedOn));
        return this;
    }

    public GroupHealthProposal markApproverApproval(String approvedBy, DateTime approvedOn, String comment, ProposalStatus status) {
        this.proposalStatus = status;
        registerEvent(new GHProposalStatusAuditEvent(this.getProposalId(), status, approvedBy, comment, approvedOn));
        markASFirstPremiumPending(approvedBy, approvedOn, comment);
        markASINForce(approvedBy, approvedOn, comment);
        return this;
    }

    public GroupHealthProposal markASFirstPremiumPending(String approvedBy, DateTime approvedOn, String comment) {
        this.proposalStatus = ProposalStatus.PENDING_FIRST_PREMIUM;
        registerEvent(new GHProposalStatusAuditEvent(this.getProposalId(), ProposalStatus.PENDING_FIRST_PREMIUM, approvedBy, comment, approvedOn));
        return this;
    }

    public GroupHealthProposal markASINForce(String approvedBy, DateTime approvedOn, String comment) {
        this.proposalStatus = ProposalStatus.IN_FORCE;
        registerEvent(new GHProposalStatusAuditEvent(this.getProposalId(), ProposalStatus.IN_FORCE, approvedBy, comment, approvedOn));
        return this;
    }

    public BigDecimal getNetAnnualPremiumPaymentAmount(GHPremiumDetail premiumDetail) {
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
    }

    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }

    public GroupHealthProposal updateWithOpportunityId(OpportunityId opportunityId) {
        this.opportunityId = opportunityId;
        return this;

    }
}
