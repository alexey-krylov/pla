package com.pla.grouphealth.sharedresource.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Samir on 5/29/2015.
 */
@Component(value = "ghAgentIsActive")
public class AgentIsActive implements ISpecification<AgentId> {

    private GHQuotationFinder ghQuotationFinder;

    @Autowired
    public AgentIsActive(GHQuotationFinder ghQuotationFinder) {
        this.ghQuotationFinder = ghQuotationFinder;
    }

    @Override
    public boolean isSatisfiedBy(AgentId candidate) {
        Map<String, Object> agentMap = ghQuotationFinder.getAgentById(candidate.getAgentId());
        return agentMap.get("agentId") != null;
    }
}
