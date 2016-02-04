package com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization;

import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.core.hcp.domain.model.HCPCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClaimantHCPDetailDto {
    private String hospitalizationEvent;
    private String hcpCode;
    private String hcpName;
    private HCPAddress hcpAddress;

    public static ClaimantHCPDetailDto getInstance() {
        return new ClaimantHCPDetailDto();
    }

    public ClaimantHCPDetailDto updateWithHospitalizationEvent(String hospitalizationEvent) {
        this.hospitalizationEvent = hospitalizationEvent;
        return this;
    }

    public ClaimantHCPDetailDto updateWithAddress(HCPAddress hcpAddress) {
        if(isNotEmpty(hcpAddress))
            this.hcpAddress = hcpAddress;
        return this;
    }

    public ClaimantHCPDetailDto updateWithHCPName(String hcpName) {
        this.hcpName = hcpName;
        return this;
    }

    public ClaimantHCPDetailDto updateWithHCPCode(HCPCode hcpCode) {
        if(isNotEmpty(hcpCode))
            this.hcpCode = hcpCode.getHcpCode();
        return null;
    }
}
