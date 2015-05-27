package com.pla.individuallife.proposal.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Preconditions;
import com.pla.individuallife.identifier.ProposalId;
import com.pla.sharedkernel.domain.model.ProposalNumber;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Document(collection = "individual_life_proposal")
public class ProposalAggregate extends AbstractAnnotatedAggregateRoot<ProposalId> {

    private final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    private ProposalSpecification specification;
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private Set<RiderDetail> riders;
    @JsonSerialize(using = ToStringSerializer.class)
    private ProposalNumber proposalNumber;
    @Id
    @AggregateIdentifier
    private ProposalId proposalId;
    private ProposalPlanDetail proposalPlanDetail;
    private List<Beneficiary> beneficiaries;
    private BigDecimal totalBeneficiaryShare = BigDecimal.ZERO;

    public ProposalAggregate() {
        riders = new HashSet<RiderDetail>();
        beneficiaries = new ArrayList<Beneficiary>();
    }

    public ProposalAggregate(ProposalNumber proposalNumber, ProposalPlanDetail proposalPlanDetail, ProposedAssured proposedAssured, Proposer proposer, Set<RiderDetail> riders) {
        this.proposalNumber = proposalNumber;
        assignProposedAssured(proposedAssured);
        assignProposer(proposer);
        assignPlan(proposalPlanDetail);
        this.riders = riders;
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

    public void addBeneficiary(Beneficiary beneficiary) {
        BigDecimal newTotal = totalBeneficiaryShare.add(beneficiary.getShare());
        Preconditions.checkArgument(newTotal.compareTo(PERCENTAGE) == -1, "Total share exceeds 100%. Cannot add any more beneficiary.");
        boolean sameBeneficiaryExists = beneficiaries.parallelStream().anyMatch(each -> (each.equals(beneficiary)));
        Preconditions.checkArgument(!sameBeneficiaryExists, "Beneficiary already exists.");
        totalBeneficiaryShare = newTotal;
    }
}
