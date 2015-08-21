package com.pla.grouplife.claim.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Mirror on 8/19/2015.
 */
@Getter
public class PolicyHolderDetail {

    private String proposerName;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailID;

    private String mobileNumber;

    private String workPhone;
}
