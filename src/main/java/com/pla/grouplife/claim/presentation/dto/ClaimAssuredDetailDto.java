package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Created by ak on 30/12/2015.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClaimAssuredDetailDto {

    private String title;

    private String firstName;

    private String surName;

    private LocalDate dateOfBirth;

    private  int ageOnNextBirthDate;

    private String nrcNumber;

    private Gender gender;

    private BigDecimal sumAssured;

    private BigDecimal  reserveAmount;

    private String category;

    private String manNumber;

    private BigDecimal lastSalary;

    private String  occupation;

    private ClaimMainAssuredDetailDto claimMainAssuredDetail;


    public ClaimAssuredDetailDto updateWithMainAssured(ClaimMainAssuredDetailDto claimMainAssuredDetail){
        this.claimMainAssuredDetail=claimMainAssuredDetail;
        return this;
    }

}
