package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimAssuredDetailDto {
    private String salutation;
    private String firstName;
    private String surname;
    private LocalDate dateOfBirth;
    private int ageNextBirthday;
    private String nrcNumber;
    private String gender;
    private String category;
    private String manNumber;
    private String clientId;
    private String mainAssuredFullName;
    private String relationshipWithMainAssured;
    private String mainAssuredNRC;
    private String mainAssuredMANNumber;
    private BigDecimal mainAssuredLastSalary;
    private String mainAssuredClientId;
    private boolean dependentAssuredDetailPresent;
}
