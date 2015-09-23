package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.*;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Mirror on 8/21/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class AssuredDetail {

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

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private FamilyId familyId;

    private Relationship relationship;
}
