package com.pla.core.application.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.AgentFinder;
import com.pla.publishedlanguage.contract.ICommissionHierarchyProvider;
import com.pla.sharedkernel.domain.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 7/9/2015.
 */
@Service(value = "commissionHierarchyProvider")
public class CommissionHierarchyProviderImpl implements ICommissionHierarchyProvider {


    @Autowired
    private AgentFinder agentFinder;
    // TODO Query for agent detail,team leader,branch manager,branch bde and regional manager as per the
    // TODO given date

    @Override
    public PolicyCommissionHierarchy getCommissionHierarchy(AgentId agentId, DateTime now) {
        List<Map<String ,Object>> policyCommissionDetail = agentFinder.findPolicyCommissionByAgentId(agentId,now);
        if (isNotEmpty(policyCommissionDetail)){
            Map<String,Object> policyCommissionMap = policyCommissionDetail.get(0);
            Agent agent = createAgent(policyCommissionMap);
            TeamLeader teamLeader = createTeamLeader(policyCommissionMap);
            BranchBDE branchBDE = createBranchBDE(policyCommissionMap);
            BranchManager branchManager = createBranchManager(policyCommissionMap);
            RegionalManager regionalManager = createRegionalManager(policyCommissionMap);
            return new PolicyCommissionHierarchy(agent,teamLeader,branchManager,branchBDE,regionalManager);
        }
        return new PolicyCommissionHierarchy();
    }

    private Agent createAgent(Map<String, Object> policyCommissionMap) {
        String agentCode = policyCommissionMap.get("agentCode")!=null? policyCommissionMap.get("agentCode").toString():null;
        String agentFirstName = policyCommissionMap.get("agentFirstName")!=null? policyCommissionMap.get("agentFirstName").toString():null;
        String agentLastName = policyCommissionMap.get("agentLastName")!=null? policyCommissionMap.get("agentLastName").toString():null;
        if (checkIfNotNull(agentFirstName, agentLastName, agentCode)){
            String fullName = agentFirstName+" "+agentLastName;
            return new Agent(agentCode,fullName);
        }
        return null;
    }

    private TeamLeader createTeamLeader(Map<String, Object> policyCommissionMap) {
        String employeeId = policyCommissionMap.get("teamLeaderEmployeeId")!=null? policyCommissionMap.get("teamLeaderEmployeeId").toString():null;
        String teamFirstName = policyCommissionMap.get("teamLeaderFirstName")!=null? policyCommissionMap.get("teamLeaderFirstName").toString():null;
        String teamLastName = policyCommissionMap.get("teamLeaderLastName")!=null? policyCommissionMap.get("teamLeaderLastName").toString():null;
        if (checkIfNotNull(teamFirstName, teamLastName, employeeId)){
            String fullName = teamFirstName+" "+teamLastName;
            return new TeamLeader(employeeId,fullName);
        }
        return null;
    }

    private BranchManager createBranchManager(Map<String, Object> policyCommissionMap) {
        String employeeId = policyCommissionMap.get("bmfEmployeeId")!=null? policyCommissionMap.get("bmfEmployeeId").toString():null;
        String branchManagerFirstName = policyCommissionMap.get("bmfFirstName")!=null? policyCommissionMap.get("bmfFirstName").toString():null;
        String branchManagerLastName = policyCommissionMap.get("bmfLastName")!=null? policyCommissionMap.get("bmfLastName").toString():null;
        if (checkIfNotNull(branchManagerFirstName, branchManagerLastName, employeeId)){
            String fullName = branchManagerFirstName+" "+branchManagerLastName;
            return new BranchManager(employeeId,fullName);
        }
        return null;
    }


    private BranchBDE createBranchBDE(Map<String, Object> policyCommissionMap) {
        String employeeId = policyCommissionMap.get("bdeEmployeeId")!=null? policyCommissionMap.get("bdeEmployeeId").toString():null;
        String branchDEManagerFirstName = policyCommissionMap.get("bdeFirstName")!=null? policyCommissionMap.get("bdeFirstName").toString():null;
        String branchDEManagerLastName = policyCommissionMap.get("bdeLastName")!=null? policyCommissionMap.get("bdeLastName").toString():null;
        if (checkIfNotNull(branchDEManagerFirstName, branchDEManagerLastName, employeeId)){
            String fullName = branchDEManagerFirstName+" "+branchDEManagerLastName;
            return new BranchBDE(employeeId,fullName);
        }
        return null;
    }

    private RegionalManager createRegionalManager(Map<String, Object> policyCommissionMap) {
        String employeeId = policyCommissionMap.get("rmfEmployeeId")!=null? policyCommissionMap.get("rmfEmployeeId").toString():null;
        String branchDEManagerFirstName = policyCommissionMap.get("rmfFirstName")!=null? policyCommissionMap.get("rmfFirstName").toString():null;
        String branchDEManagerLastName = policyCommissionMap.get("rmfLastName")!=null? policyCommissionMap.get("rmfLastName").toString():null;
        if (checkIfNotNull(branchDEManagerFirstName, branchDEManagerLastName, employeeId)){
            String fullName = branchDEManagerFirstName+" "+branchDEManagerLastName;
            return new RegionalManager(employeeId,fullName);
        }
        return null;
    }

    private boolean checkIfNotNull(String fistName,String lastName,String employeeId){
        if (fistName!=null && lastName != null && employeeId !=null)
            return true;
        return false;
    }
}
