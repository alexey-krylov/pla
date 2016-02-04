package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Author - Mohan Sharma Created on 2/3/2016.
 */
@Getter
public class UploadGroupHealthCashlessClaimCommand {
    private String hcpCode;
    private Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos;
    private DateTime batchDate;
    private String batchNumber;
    private String batchUploaderUserId;
    public UploadGroupHealthCashlessClaimCommand(String hcpCode, Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, DateTime batchDate, String batchUploaderUserId) {
        this.hcpCode = hcpCode;
        this.claimUploadedExcelDataDtos = claimUploadedExcelDataDtos;
        this.batchDate = batchDate;
        this.batchUploaderUserId = batchUploaderUserId;
    }
}
