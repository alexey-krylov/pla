package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Preconditions;
import com.pla.individuallife.identifier.ProposalId;
import com.pla.individuallife.proposal.presentation.dto.QuestionAnswerDto;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.*;

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
    private List<QuestionAnswerDto> compulsoryHealthStatement;

    ProposalAggregate() {
        riders = new HashSet<RiderDetail>();
        beneficiaries = new ArrayList<Beneficiary>();
    }

    public ProposalAggregate(UserDetails userDetails, ProposalId proposalId, String proposalNumber, ProposedAssured proposedAssured, Proposer proposer) {
        boolean hasProposalPreprocessorRole = hasIndividualLifeProposalProcessorRole(userDetails.getAuthorities());
        if (!hasProposalPreprocessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal processor(ROLE_PROPOSAL_PROCESSOR) authority");
        }
        Preconditions.checkArgument(hasProposalPreprocessorRole);
        this.proposalNumber = proposalNumber;
        this.proposalId = proposalId;
        assignProposedAssured(proposedAssured);
        assignProposer(proposer);
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

    public void updateCompulsoryHealthStatement(List<QuestionAnswerDto> compulsoryHealthStatement){

        //Update Logic
        this.compulsoryHealthStatement=compulsoryHealthStatement;
    }
}
