package com.pla.individuallife.domain.model.proposal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.model.ProposalNumber;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Document(collection = "individual_life_proposal")
public class ProposalAggregate extends AbstractAnnotatedAggregateRoot<ProposalNumber> {

    private final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    private ProposalSpecification specification;
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private Set<RiderDetail> riders;
    @JsonSerialize(using = ToStringSerializer.class)
    private ProposalNumber proposalNumber;
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
