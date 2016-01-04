package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@Getter
public class UploadPreAuthorizationCommand {
    private String hcpCode;
    private Set<PreAuthorizationDetailDto> preAuthorizationDetailDtos;
    private DateTime batchDate;
    private String batchNumber;

    public UploadPreAuthorizationCommand(String hcpCode, Set<PreAuthorizationDetailDto> preAuthorizationDetailDtos, DateTime batchDate) {
        this.hcpCode = hcpCode;
        this.preAuthorizationDetailDtos = preAuthorizationDetailDtos;
        this.batchDate = batchDate;
    }
}
