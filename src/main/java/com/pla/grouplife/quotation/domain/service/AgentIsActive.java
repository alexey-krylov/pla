package com.pla.grouplife.quotation.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by Samir on 5/29/2015.
 */
@Specification
public class AgentIsActive implements ISpecification<AgentId> {

    private GLQuotationFinder glQuotationFinder;

    @Autowired
    public AgentIsActive(GLQuotationFinder glQuotationFinder) {
        this.glQuotationFinder = glQuotationFinder;
    }

    @Override
    public boolean isSatisfiedBy(AgentId candidate) {
        Map<String, Object> agentMap = glQuotationFinder.getAgentById(candidate.getAgentId());
        return agentMap.get("agentId") != null;
    }
}
