package com.pla.quotation.application.service.grouplife;

import com.google.common.collect.Lists;
import com.pla.quotation.query.*;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 4/14/2015.
 */
@Service
public class GLQuotationService {


    private GLQuotationFinder glQuotationFinder;

    @Autowired
    public GLQuotationService(GLQuotationFinder glQuotationFinder) {
        this.glQuotationFinder = glQuotationFinder;
    }

    public HSSFWorkbook getPlanDetailExcel() {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        return hssfWorkbook;
    }

    public HSSFWorkbook getInsuredTemplateExcel() {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
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
        Map agentMap = (Map) quotation.get("agentId");
        String agentId = (String) agentMap.get("agentId");
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
        Map proposerMap = (Map) quotation.get("proposer");
        return new ProposerDto(proposerMap);
    }

    public List<GlQuotationDto> getAllQuotation() {
        return Lists.newArrayList();
    }
}
