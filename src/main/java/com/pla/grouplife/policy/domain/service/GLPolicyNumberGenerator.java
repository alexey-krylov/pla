package com.pla.grouplife.policy.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Samir on 7/8/2015.
 */
@Component
public class GLPolicyNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    private GLFinder glFinder;

    @Autowired
    public GLPolicyNumberGenerator(SequenceGenerator sequenceGenerator, GLFinder glFinder) {
        this.sequenceGenerator = sequenceGenerator;
        this.glFinder = glFinder;
    }

    public String getPolicyNumber(Class clazz, AgentId agentId) {
        String sequence = sequenceGenerator.getSequence(clazz);
        Map<String, Object> agentMap = glFinder.getAgentById(agentId.getAgentId());
        String branchCode = agentMap.get("branchCode") != null ? (String) agentMap.get("branchCode") : "BB";
        if (agentMap.get("branchCode") != null) {
            branchCode = branchCode.replaceAll("[aA-zZ]", "");
        }
        String policyNumber = "1" + "-" + "09" + "-" + branchCode + "-" + sequence;
        return policyNumber;
    }
}
