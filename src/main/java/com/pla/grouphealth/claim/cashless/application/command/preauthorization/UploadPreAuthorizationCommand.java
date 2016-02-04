package com.pla.grouphealth.claim.cashless.application.command.preauthorization;

import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimUploadedExcelDataDto;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Getter
public class UploadPreAuthorizationCommand {
    private String hcpCode;
    private Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos;
    private DateTime batchDate;
    private String batchNumber;
    private String batchUploaderUserId;

    public UploadPreAuthorizationCommand(String hcpCode, Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, DateTime batchDate, String batchUploaderUserId) {
        this.hcpCode = hcpCode;
        this.claimUploadedExcelDataDtos = claimUploadedExcelDataDtos;
        this.batchDate = batchDate;
        this.batchUploaderUserId = batchUploaderUserId;
    }
}
