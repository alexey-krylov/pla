package com.pla.grouplife.claim.domain.model;


import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import lombok.*;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

@ValueObject
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter

public class GLClaimSettlementData {

    private ClaimSettlementId claimSettlementId;
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
    private BigDecimal debitAmount;
    // private ClaimType claimType;
    private ClaimStatus claimStatus;
}
