package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.google.common.collect.Lists;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.*;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorization;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Data
public class GroupHealthCashlessClaimBatchDetailDto {
    private String batchNumber;
    private String hcpName;
    private String hcpCode;
    private LocalDate batchDate;
    private LocalDate batchClosedOnDate;
    private LocalDate batchDisbursementDate;
    private List<BatchClaimDetailDto> batchClaimDetails;
    private GroupHealthCashlessClaimBatchAccountingDetailDto accountingDetail;
    private GroupHealthCashlessClaimBatchBankDetailDto groupHealthCashlessClaimBatchBankDetail;

    /*public GroupHealthCashlessClaimBatchDetailDto updateWithDetails(GroupHealthCashlessClaimBatchDetail groupHealthCashlessClaimBatchDetail) {
        if(isNotEmpty(groupHealthCashlessClaimBatchDetail)) {
            this.batchNumber = groupHealthCashlessClaimBatchDetail.getBatchNumber();
            this.hcpName = groupHealthCashlessClaimBatchDetail.getHcpName();
            this.hcpCode = groupHealthCashlessClaimBatchDetail.getHcpCode();
            this.batchDate = groupHealthCashlessClaimBatchDetail.getBatchDate();
            this.batchClosedOnDate = groupHealthCashlessClaimBatchDetail.getBatchClosedOnDate();
            this.batchDisbursementDate = groupHealthCashlessClaimBatchDetail.getBatchDisbursementDate();
            this.batchClaimDetails = constructBatchClaimDetails(groupHealthCashlessClaimBatchDetail.getBatchClaimDetails());
            this.accountingDetail = constructAccountingDetails(groupHealthCashlessClaimBatchDetail.getAccountingDetail());
            this.groupHealthCashlessClaimBatchBankDetail = constructGroupHealthCashlessClaimBatchBankDetail(groupHealthCashlessClaimBatchDetail.getGroupHealthCashlessClaimBatchBankDetail());
        }
        return this;
    }*/

    /*private GroupHealthCashlessClaimBatchBankDetailDto constructGroupHealthCashlessClaimBatchBankDetail(GroupHealthCashlessClaimBatchBankDetail groupHealthCashlessClaimBatchBankDetail) {
        GroupHealthCashlessClaimBatchBankDetailDto groupHealthCashlessClaimBatchBankDetailDto = new GroupHealthCashlessClaimBatchBankDetailDto();
        try {
            BeanUtils.copyProperties(groupHealthCashlessClaimBatchBankDetailDto, groupHealthCashlessClaimBatchBankDetail);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return groupHealthCashlessClaimBatchBankDetailDto;
    }*/

   /* private GroupHealthCashlessClaimBatchAccountingDetailDto constructAccountingDetails(GroupHealthCashlessClaimBatchAccountingDetail accountingDetail) {
        GroupHealthCashlessClaimBatchAccountingDetailDto groupHealthCashlessClaimBatchAccountingDetailDto = new GroupHealthCashlessClaimBatchAccountingDetailDto();
        if(isNotEmpty(accountingDetail)) {
            try {
                BeanUtils.copyProperties(groupHealthCashlessClaimBatchAccountingDetailDto, accountingDetail);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return groupHealthCashlessClaimBatchAccountingDetailDto;
    }

    private List<BatchClaimDetailDto> constructBatchClaimDetails(List<BatchClaimDetail> batchClaimDetails) {
        return isNotEmpty(batchClaimDetails) ? batchClaimDetails.stream().map(claim -> new BatchClaimDetailDto().updateWithDetails(claim)).collect(Collectors.toList()) : Lists.newArrayList();
    }*/

    public GroupHealthCashlessClaimBatchDetailDto updateWithBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public GroupHealthCashlessClaimBatchDetailDto updateWithBatchDate(LocalDate claimIntimationDate) {
        this.batchDate = claimIntimationDate;
        return this;
    }

    public GroupHealthCashlessClaimBatchDetailDto updateWithBatchClosedOnDate(LocalDate batchClosedOnDate) {
        this.batchClosedOnDate = batchClosedOnDate;
        return this;
    }

    public GroupHealthCashlessClaimBatchDetailDto updateWithListOfClaims(List<GroupHealthCashlessClaim> groupHealthCashlessClaims, Map<String, BigDecimal> mapOfAgreedAmountForEachClaim) {
        if(isNotEmpty(groupHealthCashlessClaims)){
            this.batchClaimDetails = groupHealthCashlessClaims.stream().map(claim -> new BatchClaimDetailDto().updateWithClaimDetails(claim, mapOfAgreedAmountForEachClaim)).collect(Collectors.toList());
        }
        return this;
    }

    public GroupHealthCashlessClaimBatchDetailDto updateWithHCPDetails(GroupHealthCashlessClaimHCPDetail groupHealthCashlessClaimHCPDetail) {
        if(isNotEmpty(groupHealthCashlessClaimHCPDetail)){
            this.hcpCode = isNotEmpty( groupHealthCashlessClaimHCPDetail.getHcpCode()) ? groupHealthCashlessClaimHCPDetail.getHcpCode().getHcpCode() : StringUtils.EMPTY;
            this.hcpName = groupHealthCashlessClaimHCPDetail.getHcpName();
            this.groupHealthCashlessClaimBatchBankDetail = constructGroupHealthCashlessClaimBatchBankDetail(groupHealthCashlessClaimHCPDetail.getHcpBankDetail());
        }
        return this;
    }

    private GroupHealthCashlessClaimBatchBankDetailDto constructGroupHealthCashlessClaimBatchBankDetail(GroupHealthCashlessClaimHCPBankDetail groupHealthCashlessClaimHCPDetail) {
        return isNotEmpty(groupHealthCashlessClaimHCPDetail) ? new GroupHealthCashlessClaimBatchBankDetailDto().updateWithDetails(groupHealthCashlessClaimHCPDetail) : new GroupHealthCashlessClaimBatchBankDetailDto();
    }

    public GroupHealthCashlessClaimBatchDetailDto updateWithAccountingDetails(List<GroupHealthCashlessClaim> groupHealthCashlessClaims, Set<HCPServiceDetail> serviceDetails) {
        GroupHealthCashlessClaimBatchAccountingDetailDto groupHealthCashlessClaimBatchAccountingDetailDto = new GroupHealthCashlessClaimBatchAccountingDetailDto();
        if(isNotEmpty(groupHealthCashlessClaims)){
            groupHealthCashlessClaimBatchAccountingDetailDto
                    .updateWithTotalBilledAmount(groupHealthCashlessClaims)
                    .updateWithTotalApprovedAmount(groupHealthCashlessClaims)
                    .updateWithTotalServiceMismatchRejectedAmount(groupHealthCashlessClaims)
                    .updateWithTotalBillMismatchRejectedAmount(groupHealthCashlessClaims)
                    .updateWithTotalUnacknowledgeAmount(groupHealthCashlessClaims, serviceDetails)
                    .updateWithTotalNumberOfClaims(groupHealthCashlessClaims)
                    .updateWithTotalReviewedClaims(groupHealthCashlessClaims)
                    .updateWithTotalApprovedClaims(groupHealthCashlessClaims)
                    .updateWithTotalRejectedClaims(groupHealthCashlessClaims)
                    .updateWithTotalApprovedClaimsLesserThanClaimAmount(groupHealthCashlessClaims);
            //this.GroupHealthCashlessClaimBatchAccountingDetailDto
        }
        this.accountingDetail = groupHealthCashlessClaimBatchAccountingDetailDto;
        return this;
    }
}
