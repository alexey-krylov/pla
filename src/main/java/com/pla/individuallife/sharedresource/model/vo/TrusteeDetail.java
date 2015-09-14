package com.pla.individuallife.sharedresource.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 9/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrusteeDetail {
    private String tittle;
    private String firstName;
    private String surName;
    private String contactPersonName;
    private String organizationName;
    private String organizationPhoneNumber;
    private String nrc;
    private String mobileNumber;
    private String emailId;
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String town;

}
