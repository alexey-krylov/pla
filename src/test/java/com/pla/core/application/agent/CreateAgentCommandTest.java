/*
 * Copyright (c) 3/25/15 9:38 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;
import com.pla.core.domain.model.agent.AgentStatus;
import com.pla.core.query.AgentFinder;
import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class CreateAgentCommandTest {

    @Autowired
    private AgentFinder agentFinder;

    private Map<String, Object> agentDetail;

    private List<Map<String, Object>> allAgentPlans;


    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/agent/existingagentdata.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void itShouldTransFormToCreateAgentCommand() {
        agentDetail = agentFinder.getAgentById("100011");
        allAgentPlans = agentFinder.getAllAgentPlan();
        CreateAgentCommand createAgentCommand = CreateAgentCommand.transformToAgentCommand(agentDetail, allAgentPlans, Lists.newArrayList(),Lists.newArrayList());
        assertEquals("100011", createAgentCommand.getAgentId());
        assertEquals(AgentStatus.ACTIVE, createAgentCommand.getAgentStatus());
        assertEquals(OverrideCommissionApplicable.NO, createAgentCommand.getOverrideCommissionApplicable());
    }
}
