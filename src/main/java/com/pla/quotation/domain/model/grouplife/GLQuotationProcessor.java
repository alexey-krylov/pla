package com.pla.quotation.domain.model.grouplife;

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
public class GLQuotationProcessor {

    private String userName;

    public GLQuotationProcessor(String userName) {
        this.userName = userName;
    }


    public GroupLifeQuotation createGroupLifeQuotation(String quotationCreator, QuotationId quotationId, AgentId agentId, String proposerName) {
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerName);
        return GroupLifeQuotation.createWithAgentAndProposerDetail(quotationCreator, quotationId, agentId, proposerBuilder.build());
    }

    public GroupLifeQuotation updateWithAgentId(GroupLifeQuotation groupLifeQuotation, AgentId agentId) {
        return groupLifeQuotation.updateWithAgent(agentId);
    }

    public GroupLifeQuotation updateWithProposer(GroupLifeQuotation groupLifeQuotation, Proposer proposer) {
        return groupLifeQuotation.updateWithProposer(proposer);
    }

    public GroupLifeQuotation updateWithInsured(GroupLifeQuotation groupLifeQuotation, Set<Insured> insureds) {
        return groupLifeQuotation.updateWithInsured(insureds);
    }
}
