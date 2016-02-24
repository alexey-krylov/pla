package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.domain.model.claim.BatchClaimDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBatchAccountingDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBatchBankDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBatchDetail;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
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

    public GroupHealthCashlessClaimBatchDetailDto updateWithDetails(GroupHealthCashlessClaimBatchDetail groupHealthCashlessClaimBatchDetail) {
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
    }

    private GroupHealthCashlessClaimBatchBankDetailDto constructGroupHealthCashlessClaimBatchBankDetail(GroupHealthCashlessClaimBatchBankDetail groupHealthCashlessClaimBatchBankDetail) {
        GroupHealthCashlessClaimBatchBankDetailDto groupHealthCashlessClaimBatchBankDetailDto = new GroupHealthCashlessClaimBatchBankDetailDto();
        try {
            BeanUtils.copyProperties(groupHealthCashlessClaimBatchBankDetailDto, groupHealthCashlessClaimBatchBankDetail);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return groupHealthCashlessClaimBatchBankDetailDto;
    }

    private GroupHealthCashlessClaimBatchAccountingDetailDto constructAccountingDetails(GroupHealthCashlessClaimBatchAccountingDetail accountingDetail) {
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
    }
}
