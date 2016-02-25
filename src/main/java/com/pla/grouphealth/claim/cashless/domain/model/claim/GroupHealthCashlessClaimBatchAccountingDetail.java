package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimBatchAccountingDetail {
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