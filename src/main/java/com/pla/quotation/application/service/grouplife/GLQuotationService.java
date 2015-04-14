package com.pla.quotation.application.service.grouplife;

import com.google.common.collect.Lists;
import com.pla.quotation.query.AgentDetailDto;
import com.pla.quotation.query.GlQuotationDto;
import com.pla.quotation.query.PremiumDetailDto;
import com.pla.quotation.query.ProposerDto;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Samir on 4/14/2015.
 */
@Service
public class GLQuotationService {


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
        return new AgentDetailDto();
    }

    public ProposerDto getProposerDetail(QuotationId quotationId) {
        return new ProposerDto();
    }

    public List<GlQuotationDto> getAllQuotation() {
        return Lists.newArrayList();
    }
}
