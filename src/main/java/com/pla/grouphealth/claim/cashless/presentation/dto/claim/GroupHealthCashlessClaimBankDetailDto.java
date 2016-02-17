package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBankDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/17/2016.
 */
@NoArgsConstructor
@Setter
@Getter
public class GroupHealthCashlessClaimBankDetailDto {
    private String bankName;
    private String bankBranchCode;
    private String bankAccountType;
    private String bankAccountNumber;

    public GroupHealthCashlessClaimBankDetailDto updateWithDetails(GroupHealthCashlessClaimBankDetail groupHealthCashlessClaimBankDetail) {
        if(isNotEmpty(groupHealthCashlessClaimBankDetail)) {
            try {
                BeanUtils.copyProperties(this, groupHealthCashlessClaimBankDetail);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
