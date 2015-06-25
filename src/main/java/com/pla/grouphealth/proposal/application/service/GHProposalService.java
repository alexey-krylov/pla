package com.pla.grouphealth.proposal.application.service;

import com.pla.grouphealth.proposal.presentation.dto.GHProposalDto;
import com.pla.grouphealth.proposal.presentation.dto.SearchGHProposalDto;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.quotation.application.command.GHRecalculatedInsuredPremiumCommand;
import com.pla.grouphealth.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouphealth.sharedresource.dto.AgentDetailDto;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 6/24/2015.
 */
@Service
public class GHProposalService {

    private GHProposalFinder ghProposalFinder;

    private IPlanAdapter planAdapter;

    @Autowired
    public GHProposalService(GHProposalFinder ghProposalFinder, IPlanAdapter planAdapter) {
        this.ghProposalFinder = ghProposalFinder;
        this.planAdapter = planAdapter;
    }

    //TODO implement
    public boolean hasProposalForQuotation(String quotationId) {
        return false;
    }

    public List searchGeneratedQuotation(String quotationNumber) {
        return null;
    }

    public AgentDetailDto getAgentDetail(ProposalId proposalId) {
        return null;
    }

    public List<GHProposalDto> searchProposal(SearchGHProposalDto searchGHProposalDto) {
        return null;
    }

    public byte[] getPlanReadyReckoner(String proposalId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouphealth/quotation/planReadyReckoner.jrxml");
        return pdfData;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = ghProposalFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public ProposerDto getProposerDetail(ProposalId proposalId) {
        return null;
    }

    public GHPremiumDetailDto getPremiumDetail(QuotationId quotationId) {
        return null;
    }

    public GHPremiumDetailDto recalculatePremium(GHRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        return null;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String proposalId) {
        return null;
    }

    public boolean isValidInsuredTemplate(String proposalId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        return false;
    }

    public List<GHInsuredDto> transformToInsuredDto(HSSFWorkbook insuredTemplateWorkbook, String proposalId, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        return null;
    }
}
