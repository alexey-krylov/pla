package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.domain.model.PaymentMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;

/**
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class GLClaimSettlementCommand {

    private String claimId;
    private String claimNumber;
    private DateTime claimApprovedOn;
    private BigDecimal approvedAmount;
    private PaymentMode paymentMode;
    private DateTime paymentDate;
    private BigDecimal paidAmount;
    private String bankName;
    private String bankBranchName;
    private String accountType;
    private String accountNumber;
    private String instrumentNumber;
    private DateTime instrumentDate;
    private ClaimStatus claimStatus;
    private UserDetails userDetails;
}
