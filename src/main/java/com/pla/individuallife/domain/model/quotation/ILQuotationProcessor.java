package com.pla.individuallife.domain.model.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Karunakar on 5/13/2015.
 */
@EqualsAndHashCode(of = "userName")
@Getter
public class ILQuotationProcessor {

    private String userName;

    public ILQuotationProcessor(String userName) {
        this.userName = userName;
    }


    public IndividualLifeQuotation createIndividualLifeQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, String assuredTitle, String assuredFName, String assuredSurname, String assuredNRC, PlanId planid ) {
        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.getAssuredBuilder(assuredTitle, assuredFName, assuredSurname, assuredNRC);
        return IndividualLifeQuotation.createWithBasicDetail(quotationNumber, quotationCreator, quotationId, agentId, proposedAssuredBuilder.build(), planid);
    }

    public IndividualLifeQuotation updateWithProposerAndAgentId(IndividualLifeQuotation individualLifeQuotation, Proposer proposer, AgentId agentId) {
        return individualLifeQuotation.updateWithProposer(proposer, agentId);
    }

    public IndividualLifeQuotation updateWithAssured(IndividualLifeQuotation individualLifeQuotation, ProposedAssured proposedAssured, Boolean isAssuredTheProposer) {
        return individualLifeQuotation.updateWithAssured(proposedAssured, isAssuredTheProposer);
    }
}
