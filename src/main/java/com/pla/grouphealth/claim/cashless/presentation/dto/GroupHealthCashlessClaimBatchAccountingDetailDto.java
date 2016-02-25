package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimDrugService;
import lombok.Data;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.Status.*;
import static org.nthdimenzion.utils.UtilValidator.*;

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

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalBilledAmount(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        this.totalBillAmount = groupHealthCashlessClaims.stream().map(GroupHealthCashlessClaim::getTotalBilledAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalApprovedAmount(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        this.totalApprovedAmount = groupHealthCashlessClaims.stream().map(GroupHealthCashlessClaim::getTotalApprovedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalServiceMismatchRejectedAmount(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        this.totalServiceMismatchRejectedAmount = groupHealthCashlessClaims.stream()
                .filter(claim -> (claim.getStatus().equals(REPUDIATED) && claim.getClosedAtLevel().equals(SERVICE_MISMATCHED.name())))
                .map(GroupHealthCashlessClaim::getTotalBilledAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalBillMismatchRejectedAmount(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        this.totalBillMismatchRejectedAmount = groupHealthCashlessClaims.stream()
                .filter(claim -> (claim.getStatus().equals(REPUDIATED) && claim.getClosedAtLevel().equals(BILL_MISMATCHED.name())))
                .map(GroupHealthCashlessClaim::getTotalBilledAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalUnacknowledgeAmount(List<GroupHealthCashlessClaim> groupHealthCashlessClaims, Set<HCPServiceDetail> serviceDetails) {
        this.totalUnacknowledgedAmount = groupHealthCashlessClaims.stream()
                .map(claim -> getUnacknowledgedAmountOfTheClaim(claim.getGroupHealthCashlessClaimDrugServices(), serviceDetails))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    private BigDecimal getUnacknowledgedAmountOfTheClaim(Set<GroupHealthCashlessClaimDrugService> groupHealthCashlessClaimDrugServices, Set<HCPServiceDetail> serviceDetails) {
        BigDecimal unacknowledgedAmountOfTheClaim = BigDecimal.ZERO;
        if(isNotEmpty(groupHealthCashlessClaimDrugServices)){
            unacknowledgedAmountOfTheClaim = groupHealthCashlessClaimDrugServices.stream()
                    .filter(groupHealthCashlessClaimDrugService -> GroupHealthCashlessClaimDrugService.Status.IGNORE.name().equals(groupHealthCashlessClaimDrugService.getStatus()))
                    .map(drugService -> drugService.getAgreedAmountForTheService(serviceDetails))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return unacknowledgedAmountOfTheClaim;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalNumberOfClaims(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        this.totalNumberOfClaims = groupHealthCashlessClaims.size();
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalReviewedClaims(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        List<GroupHealthCashlessClaim> reviewedClaims = groupHealthCashlessClaims.stream()
                .filter(claim -> (!claim.getStatus().equals(INTIMATION) && !claim.getStatus().equals(EVALUATION) && !claim.getStatus().equals(SERVICE_MISMATCHED) && !claim.getStatus().equals(BILL_MISMATCHED)))
                .filter(ghclaim -> (ghclaim.getStatus().equals(REPUDIATED) && (!SERVICE_MISMATCHED.name().equals(ghclaim.getClosedAtLevel()) && !BILL_MISMATCHED.name().equals(ghclaim.getClosedAtLevel()))))
                .collect(Collectors.toList());
        this.totalRejectedClaims = isNotEmpty(reviewedClaims) ? reviewedClaims.size() : 0;
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalApprovedClaims(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        List<GroupHealthCashlessClaim> approvedClaims = groupHealthCashlessClaims.stream().filter(claim -> claim.getStatus().equals(APPROVED)).collect(Collectors.toList());
        this.totalApprovedClaims = isNotEmpty(approvedClaims) ? approvedClaims.size() : 0;
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalRejectedClaims(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        List<GroupHealthCashlessClaim> rejectedClaims = groupHealthCashlessClaims.stream().filter(claim -> claim.getStatus().equals(REPUDIATED)).collect(Collectors.toList());
        this.totalApprovedClaims = isNotEmpty(rejectedClaims) ? rejectedClaims.size() : 0;
        return this;
    }

    public GroupHealthCashlessClaimBatchAccountingDetailDto updateWithTotalApprovedClaimsLesserThanClaimAmount(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        List<GroupHealthCashlessClaim> claimsWithApprovedAmountLessThanClaimAmount = groupHealthCashlessClaims.stream()
                .filter(claim -> (APPROVED.equals(claim.getStatus()) || DISBURSED.equals(claim.getStatus()) || AWAITING_DISBURSEMENT.equals(claim.getStatus())))
                .filter(ghclaim -> (ghclaim.getTotalApprovedAmount().compareTo(ghclaim.getTotalBilledAmount()) == -1)).collect(Collectors.toList());
        this.totalApprovedClaimsLesserThanClaimAmount = isNotEmpty(claimsWithApprovedAmountLessThanClaimAmount) ? claimsWithApprovedAmountLessThanClaimAmount.size() : 0;
        return this;
    }
}
