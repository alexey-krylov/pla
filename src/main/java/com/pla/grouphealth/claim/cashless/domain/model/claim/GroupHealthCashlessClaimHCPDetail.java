package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantHCPDetailDto;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.lang.reflect.InvocationTargetException;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@ValueObject
@org.hibernate.annotations.Immutable
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
@Setter
public class GroupHealthCashlessClaimHCPDetail {
    private String hospitalizationEvent;
    private String hcpCode;
    private String hcpName;
    private HCPAddress hcpAddress;

    public GroupHealthCashlessClaimHCPDetail updateWithDetails(ClaimantHCPDetailDto claimantHCPDetailDto) {
        if(isNotEmpty(claimantHCPDetailDto)){
            try {
                BeanUtils.copyProperties(this, claimantHCPDetailDto);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
