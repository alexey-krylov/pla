package com.pla.grouphealth.claim.cashless.presentation.dto;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
public class ClaimantPolicyDetailDto {
    private String proposerName;
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String town;
    private String emailId;
    private String workPhone;
    private String contactPersonDetails;
    private String mobileNumber;
    public static ClaimantPolicyDetailDto getInstance() {
        return new ClaimantPolicyDetailDto();
    }
}
