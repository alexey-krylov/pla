package com.pla.grouphealth.domain.model.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * Created by Karunakar on 4/30/2015.
 */
@EqualsAndHashCode(of = "userName")
@Getter
public class GHQuotationProcessor {

    private String userName;

    public GHQuotationProcessor(String userName) {
        this.userName = userName;
    }


    public GroupHealthQuotation createGroupLifeQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, String proposerName) {
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerName);
        return GroupHealthQuotation.createWithAgentAndProposerDetail(quotationNumber, quotationCreator, quotationId, agentId, proposerBuilder.build());
    }

    public GroupHealthQuotation updateWithAgentId(GroupHealthQuotation groupHealthQuotation, AgentId agentId) {
        return groupHealthQuotation.updateWithAgent(agentId);
    }

    public GroupHealthQuotation updateWithProposer(GroupHealthQuotation groupHealthQuotation, Proposer proposer) {
        return groupHealthQuotation.updateWithProposer(proposer);
    }

    public GroupHealthQuotation updateWithInsured(GroupHealthQuotation groupHealthQuotation, Set<Insured> insureds) {
        return groupHealthQuotation.updateWithInsured(insureds);
    }
}
