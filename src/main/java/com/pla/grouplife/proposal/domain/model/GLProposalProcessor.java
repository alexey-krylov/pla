package com.pla.grouplife.proposal.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.util.GroupLifeProposalFactory;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * Created by User on 7/1/2015.
 */
@EqualsAndHashCode(of = "userName")
@Getter
public class GLProposalProcessor {

    private String userName;

    public GLProposalProcessor(String userName) {
        this.userName = userName;
    }
    public GroupLifeProposal createProposal(String quotationId, ProposalId proposalId,GroupLifeProposalFactory groupLifeProposalFactory) {
        return  groupLifeProposalFactory.createProposal(quotationId,proposalId);
    }

    public GroupLifeProposal updateWithAgentId(GroupLifeProposal groupLifeProposal, AgentId agentId) {
        return groupLifeProposal.updateWithAgentId(agentId);
    }

    public GroupLifeProposal updateWithProposer(GroupLifeProposal groupLifeProposal, Proposer proposer) {
        return groupLifeProposal.updateWithProposer(proposer);
    }

    public GroupLifeProposal updateWithInsured(GroupLifeProposal groupLifeProposal, Set
            <Insured> insureds) {
        return groupLifeProposal.updateWithInsureds(insureds);
    }

    public GroupLifeProposal updateWithPremiumDetail(GroupLifeProposal groupLifeProposal, PremiumDetail premiumDetail) {
        return groupLifeProposal.updateWithPremiumDetail(premiumDetail);
    }
}
