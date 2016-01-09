package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 1/8/2016.
 */
@Getter
@Setter
public class PreAuthorizationRequestProposerDetail {
    private String proposerName;
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String town;
    private String emailId;
    //private String
}
