package com.pla.grouphealth.policy.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Samir on 7/8/2015.
 */
@Component
public class GHPolicyNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    private GHFinder ghFinder;

    @Autowired
    public GHPolicyNumberGenerator(SequenceGenerator sequenceGenerator, GHFinder ghFinder) {
        this.sequenceGenerator = sequenceGenerator;
        this.ghFinder = ghFinder;
    }

    public String getPolicyNumber(Class clazz, AgentId agentId) {
        String sequence = sequenceGenerator.getSequence(clazz);
        Map<String, Object> agentMap = ghFinder.getAgentById(agentId.getAgentId());
        String branchCode = agentMap.get("branchCode")!=null?(String) agentMap.get("branchCode"):"BB";
        if(agentMap.get("branchCode")!=null) {
            branchCode = branchCode.replaceAll("[aA-zZ]", "");
        }
        String policyNumber = "4" + "-" + "09" + "-" + branchCode + "-" + sequence;
        return policyNumber;
    }
}
