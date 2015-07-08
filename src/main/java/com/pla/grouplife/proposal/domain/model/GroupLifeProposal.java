package com.pla.grouplife.proposal.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.Quotation;
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
@Document(collection = "group_life_proposal")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifeProposal extends AbstractAggregateRoot<ProposalId> {

    @Id
    @AggregateIdentifier
    private ProposalId proposalId;

    private Quotation quotation;

    private ProposalNumber proposalNumber;

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

    public GroupLifeProposal(ProposalId proposalId, Quotation quotation, ProposalNumber proposalNumber) {
        checkArgument(proposalId != null, "Proposal ID cannot be blank");
        checkArgument(quotation != null, "Quotation ID cannot be blank");
        checkArgument(proposalNumber != null, "Proposal Number cannot be blank");
        this.proposalId = proposalId;
        this.quotation = quotation;
        this.proposalNumber = proposalNumber;
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

    public BigDecimal getNetAnnualPremiumPaymentAmount(PremiumDetail premiumDetail) {
        BigDecimal totalBasicPremium = this.getTotalBasicPremiumForInsured();
        BigDecimal hivDiscountAmount = premiumDetail.getHivDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getHivDiscount().divide(new BigDecimal(100))));
        BigDecimal valuedClientDiscountAmount = premiumDetail.getValuedClientDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getValuedClientDiscount().divide(new BigDecimal(100))));
        BigDecimal longTermDiscountAmount = premiumDetail.getLongTermDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getLongTermDiscount().divide(new BigDecimal(100))));

        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal industryLoadingFactor = BigDecimal.ZERO;
        BigDecimal totalLoadingAmount = (addOnBenefitAmount.add(profitAndSolvencyAmount).add(industryLoadingFactor)).subtract((hivDiscountAmount.add(valuedClientDiscountAmount).add(longTermDiscountAmount)));
        BigDecimal totalInsuredPremiumAmount = totalBasicPremium.add(totalLoadingAmount);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }

    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }
}
