package com.pla.grouphealth.claim.cashless.application.service.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.claim.cashless.repository.GroupHealthCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRequestRepository;
import com.pla.sharedkernel.util.ExcelUtilityProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 2/03/2016.
 */
@DomainService
public class GroupHealthCashlessClaimService {

    @Autowired
    private GroupHealthCashlessClaimRepository groupHealthCashlessClaimRepository;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private ExcelUtilityProvider excelUtilityProvider;

    public boolean isValidInsuredTemplate(HSSFWorkbook insuredTemplateWorkbook, Map dataMap) {
        return excelUtilityProvider.isValidInsuredExcel(insuredTemplateWorkbook, GHCashlessClaimExcelHeader.getAllowedHeaders(), GHCashlessClaimExcelHeader.class, dataMap);
    }

    public GroupHealthCashlessClaim constructGroupHealthCashlessClaimEntity(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, DateTime batchDate, String batchUploaderUserId, String hcpCode) {
        return null;
    }
}
