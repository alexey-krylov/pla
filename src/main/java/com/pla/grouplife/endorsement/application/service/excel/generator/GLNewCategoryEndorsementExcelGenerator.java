package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.MasterFinder;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.OccupationCategory;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 8/10/2015.
 */

@Component
public class GLNewCategoryEndorsementExcelGenerator extends AbstractGLEndorsementExcelGenerator {

    @Autowired
    private MasterFinder masterFinder;

    @Autowired
    private IPlanAdapter planAdapter;

    @Autowired
    private GLFinder glFinder;

    @Autowired
    private GLEndorsementFinder glEndorsementFinder;

    @Override
    public HSSFWorkbook generate(PolicyId policyId, EndorsementId endorsementId) {
        List<GLEndorsementExcelHeader> excelHeaderList = GLEndorsementType.NEW_CATEGORY_RELATION.getAllowedExcelHeaders();
        List<String> excelHeaderInString = excelHeaderList.stream().map(new Function<GLEndorsementExcelHeader, String>() {
            @Override
            public String apply(GLEndorsementExcelHeader glEndorsementExcelHeader) {
                return glEndorsementExcelHeader.getDescription();
            }
        }).collect(Collectors.toList());
        Map glPolicyMap = glFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentId = (AgentId) glPolicyMap.get("agentId");
        List<PlanId> planIds = getAgentAuthorizedPlans(agentId.getAgentId());
        excelHeaderInString = getAllowedHeaders(planAdapter, planIds, excelHeaderInString);
        Map<Integer, List<String>> constraintCellDataMap = Maps.newHashMap();
        constraintCellDataMap.put(excelHeaderInString.indexOf("Gender"), Gender.getAllGender());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Relationship"), Relationship.getAllRelation());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Occupation"), getAllOccupationClassification());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Category"), OccupationCategory.getAllCategory());
        HSSFWorkbook workbook = createExcel(excelHeaderInString, Lists.newArrayList(), constraintCellDataMap);
        return workbook;
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


    private List<String> getAllowedHeaders(IPlanAdapter planAdapter, List<PlanId> planIds, List<String> headersWithoutPlanDetail) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = headersWithoutPlanDetail;
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
        }
        return ImmutableList.copyOf(headers);
    }


    private List<String> getAllOccupationClassification() {
        List<Map<String, Object>> occupationClassList = masterFinder.getAllOccupationClassification();
        List<String> occupationClasses = occupationClassList.stream().map(new Function<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> stringObjectMap) {
                return (String) stringObjectMap.get("description");
            }
        }).collect(Collectors.toList());
        return occupationClasses;
    }
}
