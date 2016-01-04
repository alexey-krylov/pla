package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.grouphealth.claim.cashless.query.PreAuthorizationFinder;
import com.pla.grouphealth.claim.cashless.repository.GHCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.sharedkernel.util.ExcelUtilityProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@DomainService
public class PreAuthorizationService {

    private GHCashlessClaimRepository ghCashlessClaimRepository;
    private PreAuthorizationRepository preAuthorizationRepository;
    private PreAuthorizationFinder preAuthorizationFinder;
    private PreAuthorizationExcelGenerator preAuthorizationExcelGenerator;
    private ExcelUtilityProvider excelUtilityProvider;
    private HCPRateRepository hcpRateRepository;

    @Autowired
    public PreAuthorizationService(GHCashlessClaimRepository ghCashlessClaimRepository, PreAuthorizationRepository preAuthorizationRepository, PreAuthorizationFinder preAuthorizationFinder, PreAuthorizationExcelGenerator preAuthorizationExcelGenerator, ExcelUtilityProvider excelUtilityProvider, HCPRateRepository hcpRateRepository) {
        this.ghCashlessClaimRepository = ghCashlessClaimRepository;
        this.preAuthorizationRepository = preAuthorizationRepository;
        this.preAuthorizationFinder = preAuthorizationFinder;
        this.preAuthorizationExcelGenerator = preAuthorizationExcelGenerator;
        this.excelUtilityProvider = excelUtilityProvider;
        this.hcpRateRepository = hcpRateRepository;
    }

    public HSSFWorkbook getGHCashlessClaimPreAuthtemplate(String hcpCode) {
        HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCode(hcpCode);
        Set<HCPServiceDetail> hcpServiceDetails = hcpRate.getHcpServiceDetails();
        List<HCPServiceDetailDto> hcpServiceDetailDtos = isNotEmpty(hcpServiceDetails) ? hcpServiceDetails.stream().map(new Function<HCPServiceDetail, HCPServiceDetailDto>() {
            @Override
            public HCPServiceDetailDto apply(HCPServiceDetail hcpServiceDetail) {
                HCPServiceDetailDto hcpServiceDetailDto = new HCPServiceDetailDto()
                        .updateWithServiceDepartment(hcpServiceDetail.getServiceDepartment())
                        .updateWithServiceAvailed(hcpServiceDetail.getServiceAvailed())
                        .updateWithNormalAmount(hcpServiceDetail.getNormalAmount())
                        .updateWithAfterHours(hcpServiceDetail.getAfterHours());
                return hcpServiceDetailDto;
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
        HSSFWorkbook hssfWorkbook = preAuthorizationExcelGenerator.generateInsuredExcel(hcpServiceDetailDtos);
        return hssfWorkbook;
    }

    public List<Map<String,Object>> getAllHcpNameAndCode(){
        List<HCPRate>hcpRates =hcpRateRepository.findAll();
        return isNotEmpty(hcpRates) ? hcpRates.stream().map(new Function<HCPRate, Map<String,Object>>() {

            @Override
            public Map<String,Object> apply(HCPRate hcpRate) {
                Map<String,Object> map=new HashMap<String,Object>();
                map.put("hcpName",hcpRate.getHcpName());
                map.put("hcpCode",hcpRate.getHcpCode().getHcpCode());
                return map;
            }
        }).collect(Collectors.toList()): Lists.newArrayList();
    }


    public boolean isValidInsuredTemplate(HSSFWorkbook insuredTemplateWorkbook) {
        return excelUtilityProvider.isValidInsuredExcel(insuredTemplateWorkbook, PreAuthorizationExcelHeader.getAllowedHeaders(), PreAuthorizationExcelHeader.class);
    }
}
