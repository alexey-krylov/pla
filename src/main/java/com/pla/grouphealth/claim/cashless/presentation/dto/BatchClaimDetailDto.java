package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.claim.*;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Data
public class BatchClaimDetailDto {
    private String claimNumber;
    private String assuredName;
    private BigDecimal billedAmount;
    private BigDecimal agreedRate;
    private BigDecimal approvedAmount;

    public BatchClaimDetailDto updateWithDetails(BatchClaimDetail batchClaimDetail) {
        if(isNotEmpty(batchClaimDetail)){
            try {
                BeanUtils.copyProperties(this, batchClaimDetail);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public BatchClaimDetailDto updateWithClaimDetails(GroupHealthCashlessClaim claim, Map<String, BigDecimal> mapOfAgreedAmountForEachClaim) {
        if(isNotEmpty(claim)){
            this.claimNumber = claim.getGroupHealthCashlessClaimId();
            this.assuredName = constructAssuredName(claim);
            this.billedAmount = claim.getTotalBilledAmount();
            this.approvedAmount = claim.getTotalApprovedAmount();
            this.agreedRate = constructAgreedRate(claim, mapOfAgreedAmountForEachClaim);
        }
        return this;
    }

    private BigDecimal constructAgreedRate(GroupHealthCashlessClaim claim, Map<String, BigDecimal> mapOfAgreedAmountForEachClaim) {
        return mapOfAgreedAmountForEachClaim.get(claim.getGroupHealthCashlessClaimId());
    }

    private String constructAssuredName(GroupHealthCashlessClaim claim) {
        StringBuffer stringBuffer = new StringBuffer();
        GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail = claim.getGroupHealthCashlessClaimPolicyDetail();
        if(isNotEmpty(groupHealthCashlessClaimPolicyDetail)){
            GroupHealthCashlessClaimAssuredDetail assuredDetail = groupHealthCashlessClaimPolicyDetail.getAssuredDetail();
            if(isNotEmpty(assuredDetail)){
                if(isNotEmpty(assuredDetail.getSalutation()))
                    stringBuffer.append(assuredDetail.getSalutation()+" ");
                if(isNotEmpty(assuredDetail.getFirstName()))
                    stringBuffer.append(assuredDetail.getFirstName()+" ");
                if(isNotEmpty(assuredDetail.getSurname()))
                    stringBuffer.append(assuredDetail.getSurname());
            }
        }
        return stringBuffer.toString();
    }
}
