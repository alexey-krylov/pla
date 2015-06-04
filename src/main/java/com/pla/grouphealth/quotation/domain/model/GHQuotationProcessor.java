package com.pla.grouphealth.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * Created by Samir on 4/7/2015.
 */
@EqualsAndHashCode(of = "userName")
@Getter
public class GHQuotationProcessor {

    private String userName;

    public GHQuotationProcessor(String userName) {
        this.userName = userName;
    }


    public GroupHealthQuotation createGroupHealthQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, String proposerName) {
        GHProposerBuilder proposerBuilder = GHProposer.getProposerBuilder(proposerName);
        return GroupHealthQuotation.createWithAgentAndProposerDetail(quotationNumber, quotationCreator, quotationId, agentId, proposerBuilder.build());
    }

    public GroupHealthQuotation updateWithAgentId(GroupHealthQuotation groupHealthQuotation, AgentId agentId) {
        return groupHealthQuotation.updateWithAgent(agentId);
    }

    public GroupHealthQuotation updateWithProposer(GroupHealthQuotation groupHealthQuotation, GHProposer proposer) {
        return groupHealthQuotation.updateWithProposer(proposer);
    }

    public GroupHealthQuotation updateWithInsured(GroupHealthQuotation groupHealthQuotation, Set<GHInsured> insureds) {
        return groupHealthQuotation.updateWithInsured(insureds);
    }

    public GroupHealthQuotation updateWithPremiumDetail(GroupHealthQuotation groupHealthQuotation, GHPremiumDetail premiumDetail) {
        return groupHealthQuotation.updateWithPremiumDetail(premiumDetail);
    }
}
