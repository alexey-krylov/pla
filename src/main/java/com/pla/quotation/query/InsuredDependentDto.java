package com.pla.quotation.query;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * Created by Samir on 4/29/2015.
 */
@Getter
@Setter
public class InsuredDependentDto {

    private PlanId insuredDependentPlan;

    private Set<CoverageId> insuredDependentCoverages;

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private Relationship relationship;

    private PlanPremiumDetailDto planPremiumDetail;

    private Set<CoveragePremiumDetailDto> coveragePremiumDetails;

}
