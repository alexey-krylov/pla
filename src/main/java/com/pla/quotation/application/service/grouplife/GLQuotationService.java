package com.pla.quotation.application.service.grouplife;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.quotation.application.command.grouplife.SearchGlQuotationDto;
import com.pla.quotation.application.service.GLInsuredExcelGenerator;
import com.pla.quotation.application.service.GLInsuredExcelParser;
import com.pla.quotation.domain.model.grouplife.Proposer;
import com.pla.quotation.presentation.dto.PlanDetailDto;
import com.pla.quotation.query.*;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 4/14/2015.
 */
@Service
public class GLQuotationService {

    private GLQuotationFinder glQuotationFinder;

    private IPlanAdapter planAdapter;

    private GLInsuredExcelGenerator glInsuredExcelGenerator;

    private GLInsuredExcelParser glInsuredExcelParser;

    @Autowired
    public GLQuotationService(GLQuotationFinder glQuotationFinder, IPlanAdapter planAdapter, GLInsuredExcelGenerator glInsuredExcelGenerator, GLInsuredExcelParser glInsuredExcelParser) {
        this.glQuotationFinder = glQuotationFinder;
        this.planAdapter = planAdapter;
        this.glInsuredExcelGenerator = glInsuredExcelGenerator;
        this.glInsuredExcelParser = glInsuredExcelParser;
    }

    public byte[] getPlanReadyReckoner(String quotationId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouplife/planReadyReckoner.jrxml");
        return pdfData;
    }

    public boolean isValidInsuredTemplate(String quotationId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelationship) {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> agentPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        return glInsuredExcelParser.isValidInsuredExcel(insuredTemplateWorkbook, samePlanForAllCategory, samePlanForAllRelationship, agentPlans);
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = glQuotationFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String quotationId) throws IOException {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<InsuredDto> insuredDtos = Lists.newArrayList();
        HSSFWorkbook hssfWorkbook = glInsuredExcelGenerator.generateInsuredExcel(insuredDtos, planIds);
        return hssfWorkbook;
    }

    public PremiumDetailDto getPremiumDetail(QuotationId quotationId) {
        return new PremiumDetailDto();
    }

    public PremiumDetailDto getReCalculatePremium(PremiumDetailDto premiumDetailDto) {
        return new PremiumDetailDto();
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
        agentDetailDto.setAgentName((String) agentDetail.get("firstName") + " " + (String) agentDetail.get("lastName"));
        return agentDetailDto;
    }

    public ProposerDto getProposerDetail(QuotationId quotationId) {
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        Proposer proposer = (Proposer) quotation.get("proposer");
        return new ProposerDto(proposer);
    }

    public List<GlQuotationDto> searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        List<Map> allQuotations = glQuotationFinder.searchQuotation(searchGlQuotationDto.getQuotationNumber(), searchGlQuotationDto.getAgentCode(), searchGlQuotationDto.getProposerName());
        List<GlQuotationDto> glQuotationDtoList = allQuotations.stream().map(new TransformToGLQuotationDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    private class TransformToGLQuotationDto implements Function<Map, GlQuotationDto> {

        @Override
        public GlQuotationDto apply(Map map) {
            String quotationId = ((ObjectId) map.get("_id")).toString();
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            Map parentQuotationIdMap = map.get("parentQuotationId") != null ? (Map) map.get("parentQuotationId") : null;
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.get("parentQuotationId") != null ? (String) parentQuotationIdMap.get("parentQuotationId") : "" : "";
            GlQuotationDto glQuotationDto = new GlQuotationDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), null, null, null, new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName, null);
            return glQuotationDto;
        }
    }
}
