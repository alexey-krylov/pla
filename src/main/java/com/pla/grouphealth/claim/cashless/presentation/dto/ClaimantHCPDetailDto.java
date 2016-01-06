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
    private String address;

    public static ClaimantHCPDetailDto getInstance() {
        return new ClaimantHCPDetailDto();
    }

    public ClaimantHCPDetailDto updateWithHospitalizationEvent(String hospitalizationEvent) {
        this.hospitalizationEvent = hospitalizationEvent;
        return this;
    }

    public ClaimantHCPDetailDto updateWithAddress(HCPAddress hcpAddress) {
        if(isNotEmpty(hcpAddress))
            setAddress(hcpAddress.getAddressLine1(), hcpAddress.getAddressLine2(), hcpAddress.getTown(), hcpAddress.getProvince(), hcpAddress.getPostalCode());
        return this;
    }

    private void setAddress(String address1, String address2, String town, String province, String postalCode) {
        StringBuffer stringBuffer = new StringBuffer();
        if(isNotEmpty(address1))
            stringBuffer.append(address1+" ");
        if(isNotEmpty(address2))
            stringBuffer.append(address2+" ");
        if(isNotEmpty(town))
            stringBuffer.append(town+" ");
        if(isNotEmpty(province))
            stringBuffer.append(province+" ");
        if(isNotEmpty(postalCode))
            stringBuffer.append(postalCode);
        this.address = stringBuffer.toString();
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
