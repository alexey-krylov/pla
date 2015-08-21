package com.pla.individuallife.policy.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Admin on 8/3/2015.
 */
@Component
public class ILPolicyNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    private ILProposalFinder ilProposalFinder;

    @Autowired
    public ILPolicyNumberGenerator(SequenceGenerator sequenceGenerator, ILProposalFinder ilProposalFinder) {
        this.sequenceGenerator = sequenceGenerator;
        this.ilProposalFinder = ilProposalFinder;
    }

    public String getPolicyNumber(Class clazz, AgentId agentId) {
        String sequence = sequenceGenerator.getSequence(clazz);
        Map<String, Object> agentMap = ilProposalFinder.getAgentByAgentId(agentId.getAgentId());
        String branchCode = (String) agentMap.get("branchCode");
        branchCode = branchCode.replaceAll("[aA-zZ]", "");
        String policyNumber = "2" + "-" + "09" + "-" + branchCode + "-" + sequence;
        return policyNumber;
    }
    
}
