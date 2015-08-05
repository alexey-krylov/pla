package com.pla.individuallife.sharedresource.model.vo;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.agent.AgentId;
import lombok.Getter;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradyumna on 22-05-2015.
 */
@ValueObject
public class AgentCommissionShareModel {

    private static final BigDecimal PERCENTAGE = new BigDecimal(100.00);
    private List<AgentCommissionShare> commissionShare = new ArrayList<AgentCommissionShare>();
    private BigDecimal totalPercentage = BigDecimal.ZERO;

    public void addAgentCommission(AgentId agentId, BigDecimal agentCommission) {
        Preconditions.checkArgument(agentId != null, "AgentId cannot be null.");
        Preconditions.checkArgument(agentCommission.compareTo(BigDecimal.ZERO) == 1, "Agent Commission cannot be zero.");
        Preconditions.checkArgument(!commissionShare.contains(agentId), "Commission share is already defined for Agent with AgentId %s", agentId);
        BigDecimal newTotal = totalPercentage.add(agentCommission);
        Preconditions.checkState(newTotal.compareTo(PERCENTAGE) <= 0);
        totalPercentage = newTotal;
        commissionShare.add(new AgentCommissionShare(agentId, agentCommission));
    }

    public List<AgentCommissionShare> getCommissionShare() {
        return commissionShare;
    }




    @Getter
    @Setter
    public class AgentCommissionShare {
        private AgentId agentId;
        private BigDecimal agentCommission;

        public AgentCommissionShare(AgentId agentId, BigDecimal agentCommission ) {
            this.agentId = agentId;
            this.agentCommission = agentCommission;
        }
    }
}
