package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 9/21/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GLInsuredDetailDto {
    private String firstName;
    private String surName;
    private String dateOfBirth;
    private String gender;
    private String nrcNumber;
    private String manNumber;
    private String clientId;
}
