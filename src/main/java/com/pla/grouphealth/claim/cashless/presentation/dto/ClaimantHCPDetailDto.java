package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPAddress;
import com.pla.core.hcp.domain.model.HCPCode;
import lombok.Getter;
import lombok.Setter;
import org.nthdimenzion.utils.UtilValidator;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@Getter
@Setter
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
