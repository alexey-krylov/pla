package com.pla.publishedlanguage.contract;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.sharedkernel.domain.model.PolicyCommissionHierarchy;
import org.joda.time.DateTime;

/**
 * Created by Samir on 7/9/2015.
 */
public interface ICommissionHierarchyProvider {

    public PolicyCommissionHierarchy getCommissionHierarchy(AgentId agentId, DateTime now);
}
