package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.claim.BatchClaimDetail;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

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
}
