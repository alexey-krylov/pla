package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.grouplife.sharedresource.service.GLInsuredExcelParser;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 8/21/2015.
 */
@Component
public class GLNewCategoryEndorsementExcelParser extends AbstractGLEndorsementExcelParser{

    @Autowired
    private GLInsuredExcelParser glInsuredExcelParser;

    @Autowired
    private GLEndorsementFinder glEndorsementFinder;

    @Autowired
    private GLFinder glFinder;


    @Override
    protected String validateRow(Row row, List<String> headers, GLEndorsementExcelValidator endorsementExcelValidator) {
        return null;
    }

    @Override
    public boolean isValidExcel(HSSFWorkbook workbook, PolicyId policyId) {
        Map policyMap = glFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentId = (AgentId) policyMap.get("agentId");
        List<PlanId> authorizedPlans = getAgentAuthorizedPlans(agentId.getAgentId());
        return glInsuredExcelParser.isValidInsuredExcel(workbook, false, false, authorizedPlans);
    }

    @Override
    public GLEndorsementInsuredDto transformExcelToGLEndorsementDto(HSSFWorkbook workbook, PolicyId policyId) {
        Map policyMap = glFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentId = (AgentId) policyMap.get("agentId");
        List<PlanId> authorizedPlans = getAgentAuthorizedPlans(agentId.getAgentId());
        List<InsuredDto> insuredDtos = glInsuredExcelParser.transformToInsuredDto(workbook, authorizedPlans);
        GLEndorsementInsuredDto glEndorsementInsuredDto = new GLEndorsementInsuredDto();
        glEndorsementInsuredDto.setInsureds(insuredDtos);
        return glEndorsementInsuredDto;
    }


    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = glEndorsementFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

}
