package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.presentation.utility.ExcelUtilityProvider;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.grouphealth.claim.cashless.query.GHCashlessClaimFinder;
import com.pla.grouphealth.claim.cashless.repository.GHCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@DomainService
public class GHCashlessClaimService {

    private GHCashlessClaimRepository ghCashlessClaimRepository;
    private PreAuthorizationRepository preAuthorizationRepository;
    private GHCashlessClaimFinder ghCashlessClaimFinder;
    private GHCashlessClaimPreAuthExcelGenerator ghCashlessClaimPreAuthExcelGenerator;
    private ExcelUtilityProvider excelUtilityProvider;
    private HCPRateRepository hcpRateRepository;

    @Autowired
    public GHCashlessClaimService(GHCashlessClaimRepository ghCashlessClaimRepository, PreAuthorizationRepository preAuthorizationRepository, GHCashlessClaimFinder ghCashlessClaimFinder, GHCashlessClaimPreAuthExcelGenerator ghCashlessClaimPreAuthExcelGenerator, ExcelUtilityProvider excelUtilityProvider, HCPRateRepository hcpRateRepository) {
        this.ghCashlessClaimRepository = ghCashlessClaimRepository;
        this.preAuthorizationRepository = preAuthorizationRepository;
        this.ghCashlessClaimFinder = ghCashlessClaimFinder;
        this.ghCashlessClaimPreAuthExcelGenerator = ghCashlessClaimPreAuthExcelGenerator;
        this.excelUtilityProvider = excelUtilityProvider;
        this.hcpRateRepository = hcpRateRepository;
    }

    public HSSFWorkbook getGHCashlessClaimPreAuthtemplate(String hcpCode) {
        /*HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCode(hcpCode);
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
        HSSFWorkbook hssfWorkbook = hcpRateExcelGenerator.generateInsuredExcel(hcpServiceDetailDtos);
        return hssfWorkbook;*/
        return null;
    }

    public List<Map<String,Object>> getAllHcpNameAndCode(){
        List<HCPRate>hcpRates =hcpRateRepository.findAll();
        return isNotEmpty(hcpRates) ? hcpRates.stream().map(new Function<HCPRate, Map<String,Object>>() {

            @Override
            public Map<String,Object> apply(HCPRate hcpRate) {
                Map<String,Object> map=new HashMap<String,Object>();
                map.put("hcpName",hcpRate.getHcpName());
                map.put("hcpCode",hcpRate.getHcpCode());
                return map;
            }
        }).collect(Collectors.toList()): Lists.newArrayList();
    }


}
