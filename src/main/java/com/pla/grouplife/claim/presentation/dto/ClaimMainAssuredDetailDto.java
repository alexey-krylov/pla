package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nthdimensioncompany on 30/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimMainAssuredDetailDto {

    private String fullName;

    private String relationship;

    private String nrcNumber;

    private String manNumber;

    private String lastSalary;
}
