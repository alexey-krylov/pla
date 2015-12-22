package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 9/21/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class AssuredSearchDto {

    private String category;
    private String relationShip;
    private String policyNumber;
    private String policyHolderName;

    /*
    private String firstName;
    private String surName;
    private String dateOfBirth;
    private String clientId;
    private String nrcNumber;
    private String manNumber;
    private Gender gender;
     */
}
