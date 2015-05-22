package com.pla.individuallife.domain.model.proposal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.pla.individuallife.domain.policy.ProposalSpecification;
import com.pla.sharedkernel.domain.model.ProposalNumber;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Document(collection = "individual_life_proposal")
@Getter
@ToString(exclude = {"logger"})
@EqualsAndHashCode(exclude = {"logger", "specification"}, callSuper = false, doNotUseGetters = true)
public class ProposalAggregate extends AbstractAnnotatedAggregateRoot<ProposalNumber> {

    ProposalSpecification specification;
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private ProposalPlanDetail planDetail;
    private Set<RiderDetail> riders;
    @JsonSerialize(using = ToStringSerializer.class)
    private ProposalNumber proposalNumber;

    public void assignProposedAssured(ProposedAssured proposedAssured) {
        specification.checkProposedAssured(proposedAssured);
        this.proposedAssured = proposedAssured;
        if (proposedAssured.isProposer()) {
            ProposerBuilder proposerBuilder = new ProposerBuilder();
            proposerBuilder.withDateOfBirth(proposedAssured.getDateOfBirth());
            proposerBuilder.withEmailAddress(proposedAssured.getEmailAddress());
            proposerBuilder.withEmploymentDetail(proposedAssured.getEmploymentDetail());
            proposerBuilder.withFirstName(proposedAssured.getFirstName());
            proposerBuilder.withSurname(proposedAssured.getSurname());
            proposerBuilder.withGender(proposedAssured.getGender());
            proposerBuilder.withMaritalStatus(proposedAssured.getMaritalStatus());
            proposerBuilder.withMobileNumber(proposedAssured.getMobileNumber());
            proposerBuilder.withEmploymentDetail(proposedAssured.getEmploymentDetail());
            proposerBuilder.withResidentialAddress(proposedAssured.getResidentialAddress());
            proposerBuilder.withTitle(proposedAssured.getTitle());
            proposerBuilder.withSpouseFirstName(proposedAssured.getSpouseFirstName());
            proposerBuilder.withSpouseLastName(proposedAssured.getSpouseLastName());
            proposerBuilder.withSpouseEmailAddress(proposedAssured.getSpouseEmailAddress());
            this.proposer = proposerBuilder.createProposer();
        }
    }

    public void assignPlan(PlanId planId) {

    }

}
