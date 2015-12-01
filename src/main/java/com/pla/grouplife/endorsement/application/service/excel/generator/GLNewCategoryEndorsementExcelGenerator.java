package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.MasterFinder;
import com.pla.grouplife.endorsement.domain.model.GLEndorsement;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.sharedresource.dto.AgentDetailDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.OccupationCategory;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

    @Autowired
    private GLQuotationFinder glQuotationFinder;


    @Override
    public HSSFWorkbook generate(PolicyId policyId, EndorsementId endorsementId) throws IOException {
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
        List<InsuredDto> insuredDtos =  getInsuredTemplateExcel(endorsementId.getEndorsementId());
        List<Map<Integer, String>> rowCellData = Lists.newArrayList();
        for (InsuredDto insuredDto : insuredDtos){
            Map<Integer, String> insuredDetailMap = Maps.newLinkedHashMap();
            if (insuredDto.getCategory()!=null) {
                for (GLEndorsementExcelHeader glEndorsementExcelHeader : excelHeaderList) {
                    glEndorsementExcelHeader.getInsuredDetail(insuredDetailMap, insuredDto, excelHeaderInString);
                }
                rowCellData.add(insuredDetailMap);
            }

            for (InsuredDto.InsuredDependentDto insuredDependentDto : insuredDto.getInsuredDependents()){
                Map<Integer, String> insuredDependentDetailMap = Maps.newLinkedHashMap();
                for (GLEndorsementExcelHeader glEndorsementExcelHeader : excelHeaderList) {
                    glEndorsementExcelHeader.getInsuredDependentDetail(insuredDependentDetailMap, insuredDependentDto, excelHeaderInString);
                }
                rowCellData.add(insuredDependentDetailMap);
            }

        }
        constraintCellDataMap.put(excelHeaderInString.indexOf("Gender"), Gender.getAllGender());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Relationship"), Relationship.getAllRelation());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Occupation"), getAllOccupationClassification());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Category"), OccupationCategory.getAllCategory());
        HSSFWorkbook workbook = createExcel(excelHeaderInString, rowCellData, constraintCellDataMap);
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

    public  List<InsuredDto> getInsuredTemplateExcel(String endorsementId) throws IOException {
        Map endorsementMap =  glEndorsementFinder.findEndorsementById(endorsementId);
        GLEndorsement glEndorsement = endorsementMap.get("endorsement")!=null?(GLEndorsement) endorsementMap.get("endorsement"):null;
        if (glEndorsement==null){
            return Lists.newArrayList();
        }
        List<Insured> insureds = Lists.newArrayList(glEndorsement.getNewCategoryRelationEndorsement().getInsureds());
        List<InsuredDto> insuredDtoList = isNotEmpty(insureds) ? insureds.stream().map(new Function<Insured, InsuredDto>() {
            @Override
            public InsuredDto apply(Insured insured) {
                InsuredDto insuredDto = new InsuredDto();
                insuredDto.setCompanyName(insured.getCompanyName());
                insuredDto.setManNumber(insured.getManNumber());
                insuredDto.setNrcNumber(insured.getNrcNumber());
                insuredDto.setSalutation(insured.getSalutation());
                insuredDto.setFirstName(insured.getFirstName());
                insuredDto.setLastName(insured.getLastName());
                insuredDto.setDateOfBirth(insured.getDateOfBirth());
                insuredDto.setGender(insured.getGender());
                insuredDto.setCategory(insured.getCategory());
                insuredDto.setAnnualIncome(insured.getAnnualIncome());
                insuredDto.setOccupationClass(insured.getOccupationClass());
                insuredDto.setOccupationCategory(insured.getCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                if (planPremiumDetail != null) {
                    InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                    insuredDto = insuredDto.addPlanPremiumDetail(planPremiumDetailDto);
                    List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetailDtoList = isNotEmpty(insured.getCoveragePremiumDetails()) ? insured.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, InsuredDto.CoveragePremiumDetailDto>() {
                        @Override
                        public InsuredDto.CoveragePremiumDetailDto apply(CoveragePremiumDetail coveragePremiumDetail) {
                            InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                    coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured());
                            return coveragePremiumDetailDto;
                        }
                    }).collect(Collectors.toList()) : Lists.newArrayList();
                    insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetailDtoList);
                }
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoList = isNotEmpty(insured.getInsuredDependents()) ? insured.getInsuredDependents().stream().map(new Function<InsuredDependent, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(InsuredDependent insuredDependent) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = new InsuredDto.InsuredDependentDto();
                        insuredDependentDto.setCompanyName(insuredDependent.getCompanyName());
                        insuredDependentDto.setManNumber(insuredDependent.getManNumber());
                        insuredDependentDto.setNrcNumber(insuredDependent.getNrcNumber());
                        insuredDependentDto.setSalutation(insuredDependent.getSalutation());
                        insuredDependentDto.setFirstName(insuredDependent.getFirstName());
                        insuredDependentDto.setLastName(insuredDependent.getLastName());
                        insuredDependentDto.setDateOfBirth(insuredDependent.getDateOfBirth());
                        insuredDependentDto.setRelationship(insuredDependent.getRelationship());
                        insuredDependentDto.setGender(insuredDependent.getGender());
                        insuredDependentDto.setCategory(insuredDependent.getCategory());
                        insuredDependentDto.setOccupationClass(insuredDependent.getOccupationClass());
                        insuredDependentDto.setOccupationCategory(insuredDependent.getCategory());
                        insuredDependentDto.setNoOfAssured(insuredDependent.getNoOfAssured());
                        PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        if (planPremiumDetail != null) {
                            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                            insuredDependentDto = insuredDependentDto.addPlanPremiumDetail(planPremiumDetailDto);
                            List<InsuredDto.CoveragePremiumDetailDto> dependentCoveragePremiumDetailDtoList = isNotEmpty(insuredDependent.getCoveragePremiumDetails()) ? insuredDependent.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, InsuredDto.CoveragePremiumDetailDto>() {
                                @Override
                                public InsuredDto.CoveragePremiumDetailDto apply(CoveragePremiumDetail coveragePremiumDetail) {
                                    InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                            coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured());
                                    return coveragePremiumDetailDto;
                                }
                            }).collect(Collectors.toList()) : Lists.newArrayList();
                            insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(dependentCoveragePremiumDetailDtoList);
                        }
                        return insuredDependentDto;
                    }
                }).collect(Collectors.toSet()) : Sets.newHashSet();
                insuredDto = insuredDto.addInsuredDependent(insuredDependentDtoList);
                return insuredDto;
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
        return insuredDtoList;
    }



    public AgentDetailDto getAgentDetail(QuotationId quotationId) {
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = glQuotationFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }

}
