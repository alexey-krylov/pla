package com.pla.grouphealth.application.service.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.application.command.quotation.SearchGHQuotationDto;
import com.pla.grouphealth.domain.model.quotation.Proposer;
import com.pla.grouphealth.query.*;
import com.pla.grouphealth.query.GHQuotationDto;
import com.pla.grouphealth.query.GHQuotationFinder;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Service
public class GHQuotationService {


    private GHQuotationFinder ghQuotationFinder;

    @Autowired
    public GHQuotationService(GHQuotationFinder ghQuotationFinder) {
        this.ghQuotationFinder = ghQuotationFinder;
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
        Map quotation = ghQuotationFinder.getQuotationById(quotationId.getQuotationId());
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = ghQuotationFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName((String) agentDetail.get("firstName") + " " + (String) agentDetail.get("lastName"));
        return agentDetailDto;
    }

    public ProposerDto getProposerDetail(QuotationId quotationId) {
        Map quotation = ghQuotationFinder.getQuotationById(quotationId.getQuotationId());
        Proposer proposer = (Proposer) quotation.get("proposer");
        return new ProposerDto(proposer);
    }

    public List<GHQuotationDto> getAllQuotation() {
        List<Map> allQuotations = ghQuotationFinder.getAllQuotation();
        List<GHQuotationDto> GHQuotationDtoList = allQuotations.stream().map(new TransformToGHQuotationDto()).collect(Collectors.toList());
        return GHQuotationDtoList;
    }


    public List<GHQuotationDto> searchQuotation(SearchGHQuotationDto searchGHQuotationDto) {
        List<Map> allQuotations = ghQuotationFinder.searchQuotation(searchGHQuotationDto.getQuotationNumber(), searchGHQuotationDto.getAgentCode(), searchGHQuotationDto.getProposerName());
        List<GHQuotationDto> GHQuotationDtoList = allQuotations.stream().map(new TransformToGHQuotationDto()).collect(Collectors.toList());
        return GHQuotationDtoList;
    }

    private class TransformToGHQuotationDto implements Function<Map, GHQuotationDto> {

        @Override
        public GHQuotationDto apply(Map map) {
            String quotationId = ((ObjectId) map.get("_id")).toString();
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            Map parentQuotationIdMap = map.get("parentQuotationId") != null ? (Map) map.get("parentQuotationId") : null;
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.get("parentQuotationId") != null ? (String) parentQuotationIdMap.get("parentQuotationId") : "" : "";
            GHQuotationDto GHQuotationDto = new GHQuotationDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), null, null, null, new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName,null);
            return GHQuotationDto;
        }
    }
}
