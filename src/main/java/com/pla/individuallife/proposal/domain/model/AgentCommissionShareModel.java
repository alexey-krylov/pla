package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.agent.AgentId;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradyumna on 22-05-2015.
 */
@ValueObject
public class AgentCommissionShareModel {

    private static final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    private final Map<AgentId, BigDecimal> commissionShare;
    private BigDecimal totalPercentage = BigDecimal.ZERO;

    public AgentCommissionShareModel() {
        this.commissionShare = new HashMap<AgentId, BigDecimal>();
    }

    public void addAgentCommission(AgentId agentId, BigDecimal agentCommission) {
        Preconditions.checkArgument(agentId != null, "AgentId cannot be null.");
        Preconditions.checkArgument(agentCommission.compareTo(BigDecimal.ZERO) == 1, "Agent Commission cannot be zero.");
        Preconditions.checkArgument(!commissionShare.containsKey(agentId), "Commission share is already defined for Agent with AgentId %s", agentId);
        BigDecimal newTotal = totalPercentage.add(agentCommission);
        Preconditions.checkState(newTotal.compareTo(PERCENTAGE) == -1);
        totalPercentage = newTotal;
        commissionShare.put(agentId, agentCommission);
    }

    public Map<AgentId, BigDecimal> getCommissionShare() {
        return commissionShare;
    }
}
