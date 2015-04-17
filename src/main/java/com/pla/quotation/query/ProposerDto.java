package com.pla.quotation.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samir on 4/9/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposerDto {

    private String proposerName;

    private String proposerCode;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    private String contactPersonName;

    private String contactPersonEmail;

    private String contactPersonMobileNumber;

    private String contactPersonWorkPhoneNumber;

    public ProposerDto(Map proposerMap) {
        Map contactDetailMap = proposerMap.get("contactDetail") != null ? (Map) proposerMap.get("contactDetail") : new HashMap<>();
        Map contactPersonDetailMap = contactDetailMap.get("contactPersonDetail") != null ? (Map) contactDetailMap.get("contactPersonDetail") : new HashMap<>();
        this.proposerName = proposerMap.get("proposerName") != null ? (String) proposerMap.get("proposerName") : "";
        this.postalCode = proposerMap.get("proposerCode") != null ? (String) proposerMap.get("proposerCode") : "";
        this.addressLine1 = contactDetailMap.get("addressLine1") != null ? (String) contactDetailMap.get("addressLine1") : "";
        this.addressLine2 = contactDetailMap.get("addressLine2") != null ? (String) contactDetailMap.get("addressLine2") : "";
        this.postalCode = contactDetailMap.get("postalCode") != null ? (String) contactDetailMap.get("postalCode") : "";
        this.province = contactDetailMap.get("province") != null ? (String) contactDetailMap.get("province") : "";
        this.town = contactDetailMap.get("town") != null ? (String) contactDetailMap.get("town") : "";
        this.emailAddress = contactDetailMap.get("emailAddress") != null ? (String) contactDetailMap.get("emailAddress") : "";
        this.contactPersonName = contactPersonDetailMap.get("contactPersonName") != null ? (String) contactDetailMap.get("contactPersonName") : "";
        this.contactPersonEmail = contactPersonDetailMap.get("contactPersonEmail") != null ? (String) contactDetailMap.get("contactPersonEmail") : "";
        this.contactPersonMobileNumber = contactPersonDetailMap.get("mobileNumber") != null ? (String) contactDetailMap.get("mobileNumber") : "";
        this.contactPersonWorkPhoneNumber = contactPersonDetailMap.get("workPhoneNumber") != null ? (String) contactDetailMap.get("workPhoneNumber") : "";
    }
}
