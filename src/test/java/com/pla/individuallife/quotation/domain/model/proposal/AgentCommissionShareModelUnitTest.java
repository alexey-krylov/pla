package com.pla.individuallife.quotation.domain.model.proposal;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.proposal.domain.model.AgentCommissionShareModel;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by ASUS on 22-May-15.
 */
public class AgentCommissionShareModelUnitTest {

    @Before
    public void setup() {
    }

    @Test(expected = IllegalArgumentException.class)
     public void whenAgentIdIsNull_thenItshouldFail() {
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionShareModel.addAgentCommission(null, BigDecimal.ONE);
    }

    @Test(expected = IllegalArgumentException.class)
     public void whenAgentCommisionIsNull_thenItshouldFail()
    {
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionShareModel.addAgentCommission(new AgentId("123"), BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenBothAreNull_thenItshouldFail() {
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionShareModel.addAgentCommission(null, null);
    }
}
