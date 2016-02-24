package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Data
public class SearchClaimSettlementDetailDto {
    private String batchNumber;
    private String hcpName;
    private String hcpCode;
    private LocalDate batchDate;
    private LocalDate batchClosedOnDate;
    private String batchStatus;
    private boolean showModalWin;
    private String errorMessage;

    public void updateWithDetails(GroupHealthCashlessClaim groupHealthCashlessClaim, String batchStatus) {
        this.batchNumber = groupHealthCashlessClaim.getBatchNumber();
        this.hcpName = isNotEmpty(groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail())? groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail().getHcpName() : StringUtils.EMPTY;
        this.hcpCode = isNotEmpty(groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail())? isNotEmpty(groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail().getHcpCode()) ? groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail().getHcpCode().getHcpCode() : StringUtils.EMPTY : StringUtils.EMPTY;
        this.batchDate = groupHealthCashlessClaim.getClaimIntimationDate();
        this.batchClosedOnDate = groupHealthCashlessClaim.getBatchClosedOnDate();
        this.batchStatus = batchStatus;
    }
}
