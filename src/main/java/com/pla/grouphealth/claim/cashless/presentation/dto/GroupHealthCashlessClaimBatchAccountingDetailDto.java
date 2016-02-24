package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Data
public class GroupHealthCashlessClaimBatchAccountingDetailDto {
    private BigDecimal totalInvoiceAmount;
    private BigDecimal totalBillAmount;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalServiceMismatchRejectedAmount;
    private BigDecimal totalBillMismatchRejectedAmount;
    private BigDecimal totalUnacknowledgedAmount;
    private int totalNumberOfClaims;
    private int totalReviewedClaims;
    private int totalApprovedClaims;
    private int totalRejectedClaims;
    private int totalApprovedClaimsLesserThanClaimAmount;
}
