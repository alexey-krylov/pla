package com.pla.core.application.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.publishedlanguage.contract.ICommissionHierarchyProvider;
import com.pla.sharedkernel.domain.model.PolicyCommissionHierarchy;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * Created by Samir on 7/9/2015.
 */
@Service(value = "commissionHierarchyProvider")
public class CommissionHierarchyProviderImpl implements ICommissionHierarchyProvider {


    // TODO Query for agent detail,team leader,branch manager,branch bde and regional manager as per the
    // TODO given date

    @Override
    public PolicyCommissionHierarchy getCommissionHierarchy(AgentId agentId, DateTime now) {
        return new PolicyCommissionHierarchy();
    }
}
