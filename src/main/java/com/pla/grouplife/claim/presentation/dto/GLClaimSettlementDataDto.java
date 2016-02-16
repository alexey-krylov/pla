package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by ak on 12/2/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GLClaimSettlementDataDto {

    private DateTime claimApprovedOn;
    private BigDecimal approvedAmount;
    private String paymentMode;
    private DateTime paymentDate;
    private BigDecimal paidAmount;
    private String bankName;
    private String bankBranchName;
    private String accountType;
    private String accountNumber;
    private String instrumentNumber;
    private DateTime instrumentDate;

}
