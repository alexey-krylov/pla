package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by ak
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GLAssuredSearchDto {
    private AssuredSearchDto assuredSearchDto;
    private String policyNumber;
    private String firstName;
    private String surName;
    private String dateOfBirth;
    private String clientId;
    private String nrcNumber;
    private String manNumber;
    private Gender gender;
    }

