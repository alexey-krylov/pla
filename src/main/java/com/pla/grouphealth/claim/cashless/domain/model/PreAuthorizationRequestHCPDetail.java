package com.pla.grouphealth.claim.cashless.domain.model;

import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantHCPDetailDto;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;

import java.lang.reflect.InvocationTargetException;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 1/9/2016.
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
