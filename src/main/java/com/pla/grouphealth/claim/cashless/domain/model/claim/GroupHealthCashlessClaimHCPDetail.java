package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.core.hcp.domain.model.HCPCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimHCPDetail {
    private String hospitalizationEvent;
    private HCPCode hcpCode;
    private String hcpName;
    private HCPAddress hcpAddress;

    public GroupHealthCashlessClaimHCPDetail updateWithHospitalizationEvent(String hospitalizationEvent) {
        this.hospitalizationEvent = hospitalizationEvent;
        return this;
    }

    public GroupHealthCashlessClaimHCPDetail updateWithHCPDetails(HCP hcp) {
        if(isNotEmpty(hcp)){
            this.hcpCode = hcp.getHcpCode();
            this.hcpName = hcp.getHcpName();
            this.hcpAddress = hcp.getHcpAddress();
        }
        return this;
    }
}
