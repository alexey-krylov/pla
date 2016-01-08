package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Rudra on 1/7/2016.
 */
@Getter
@Setter
public class PreAuthorizationDto {
    private String policyNumber;
    private String clientId;
    private String preAuthorizationId;
    private DateTime consultationDate;
    private String hcpName;
    private String policyHolderName;

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setPreAuthorizationId(String preAuthorizationId) {
        this.preAuthorizationId = preAuthorizationId;
    }

    public void setConsultationDate(DateTime consultationDate) {
        this.consultationDate = consultationDate;
    }

    public void setHcpName(String hcpName) {
        this.hcpName = hcpName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        this.policyHolderName = policyHolderName;
    }
}
