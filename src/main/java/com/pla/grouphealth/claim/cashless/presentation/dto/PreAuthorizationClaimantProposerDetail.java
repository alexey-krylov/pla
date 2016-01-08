package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreAuthorizationClaimantProposerDetail {
    String proposerName;
    String address1;
    String address2;
    String postalCode;
    String province;
    String town;
    String emailId;
    String workPhone;
    String contactPersonName;
    String contactPersonWorkPhone;
    String contactPersonMobileNumber;
    String contactPersonEmailId;

    public PreAuthorizationClaimantProposerDetail() {
    }
}