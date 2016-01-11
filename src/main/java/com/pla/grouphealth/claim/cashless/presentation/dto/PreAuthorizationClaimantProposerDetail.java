package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationClaimantProposerDetail {
    String proposerName;
    String proposerCode;
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
}