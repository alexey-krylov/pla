package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Sets;
import com.pla.core.hcp.application.service.HCPRateExcelGenerator;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.presentation.utility.ExcelUtilityProvider;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.grouphealth.claim.cashless.query.GHCashlessClaimFinder;
import com.pla.grouphealth.claim.cashless.repository.GHCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.*;

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
}
