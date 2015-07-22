package com.pla.grouphealth.policy.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHPremiumDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.identifier.PolicyId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Samir on 7/7/2015.
 */
@Document(collection = "group_health_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupHealthPolicy extends AbstractAggregateRoot<PolicyId> {

    @Id
    @AggregateIdentifier
    private PolicyId policyId;

    private Proposal proposal;

    private PolicyNumber policyNumber;

    private DateTime inceptionOn;

    private DateTime expiredOn;

    private AgentId agentId;

    private GHProposer proposer;

    private Set<GHInsured> insureds;

    private GHPremiumDetail premiumDetail;

    private PolicyStatus status;

    public GroupHealthPolicy(PolicyId policyId, PolicyNumber policyNumber, Proposal proposal, DateTime inceptionOn, DateTime expiredOn) {
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
        this.status = PolicyStatus.IN_FORCE;
    }

    public GroupHealthPolicy addAgentId(AgentId agentId) {
        this.agentId = agentId;
        return this;
    }

    public GroupHealthPolicy addProposer(GHProposer ghProposer) {
        this.proposer = ghProposer;
        return this;
    }

    public GroupHealthPolicy addInsured(Set<GHInsured> insureds) {
        this.insureds = insureds;
        return this;
    }

    public GroupHealthPolicy addPremium(GHPremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }

    @Override
    public PolicyId getIdentifier() {
        return policyId;
    }
}
