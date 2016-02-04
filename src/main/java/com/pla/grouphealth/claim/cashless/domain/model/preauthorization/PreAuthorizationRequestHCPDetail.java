package com.pla.grouphealth.claim.cashless.domain.model.preauthorization;

import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimantHCPDetailDto;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

import java.lang.reflect.InvocationTargetException;

import static org.nthdimenzion.utils.UtilValidator.*;

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
public class PreAuthorizationRequestHCPDetail {
    private String hospitalizationEvent;
    private String hcpCode;
    private String hcpName;
    private HCPAddress hcpAddress;

    public PreAuthorizationRequestHCPDetail updateWithDetails(ClaimantHCPDetailDto claimantHCPDetailDto) {
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
