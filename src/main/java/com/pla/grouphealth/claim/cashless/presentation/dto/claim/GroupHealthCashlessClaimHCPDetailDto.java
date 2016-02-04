package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimHCPDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimHCPDetailDto {
    private String hospitalizationEvent;
    private HCPCode hcpCode;
    private String hcpName;
    private HCPAddress hcpAddress;

    public GroupHealthCashlessClaimHCPDetailDto updateWithDetails(GroupHealthCashlessClaimHCPDetail groupHealthCashlessClaimHCPDetail) {
        try {
            BeanUtils.copyProperties(this, groupHealthCashlessClaimHCPDetail);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this;
    }
}
