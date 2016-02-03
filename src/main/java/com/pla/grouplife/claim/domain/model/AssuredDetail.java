package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.*;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Mirror on 8/21/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class AssuredDetail {
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

    private MainAssuredDetails mainAssuredDetails;
/*
    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private BigDecimal annualIncome;

    // private BigDecimal sumAssured;

    //private BigDecimal reserveAmount;

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private FamilyId familyId;

    private Relationship relationship;  */




}
