package com.pla.grouphealth.proposal.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHPremiumDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;

import java.util.Set;

/**
 * Created by Samir on 7/6/2015.
 */
public class GHProposalProcessor {

    private String userName;

    public GHProposalProcessor(String userName) {
        this.userName = userName;
    }


    public GroupHealthProposal updateWithAgentId(GroupHealthProposal groupHealthProposal, AgentId agentId) {
        return groupHealthProposal.updateWithAgentId(agentId);
    }

    public GroupHealthProposal updateWithProposer(GroupHealthProposal groupHealthProposal, GHProposer proposer) {
        return groupHealthProposal.updateWithProposer(proposer);
    }

    public GroupHealthProposal updateWithInsured(GroupHealthProposal groupHealthProposal, Set<GHInsured> insureds) {
        return groupHealthProposal.updateWithInsureds(insureds);
    }

    public GroupHealthProposal updateWithPremiumDetail(GroupHealthProposal groupHealthProposal, GHPremiumDetail premiumDetail) {
        return groupHealthProposal.updateWithPremiumDetail(premiumDetail);
    }
}
