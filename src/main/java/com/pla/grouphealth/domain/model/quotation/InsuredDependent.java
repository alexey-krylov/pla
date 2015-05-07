package com.pla.grouphealth.domain.model.quotation;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 4/30/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
class InsuredDependent {

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

    private Set<Policy> dependentPolicies;

    InsuredDependent(InsuredDependentBuilder insuredDependentBuilder) {
        checkArgument(insuredDependentBuilder != null);
        checkArgument(isNotEmpty(insuredDependentBuilder.getPolicies()));
        this.insuredDependentPlan = insuredDependentBuilder.getInsuredPlan();
        this.insuredDependentCoverages = insuredDependentBuilder.getInsuredCoverages();
        this.companyName = insuredDependentBuilder.getCompanyName();
        this.manNumber = insuredDependentBuilder.getManNumber();
        this.salutation = insuredDependentBuilder.getSalutation();
        this.nrcNumber = insuredDependentBuilder.getNrcNumber();
        this.firstName = insuredDependentBuilder.getFirstName();
        this.lastName = insuredDependentBuilder.getLastName();
        this.dateOfBirth = insuredDependentBuilder.getDateOfBirth();
        this.gender = insuredDependentBuilder.getGender();
        this.category = insuredDependentBuilder.getCategory();
        this.dependentPolicies = insuredDependentBuilder.getPolicies();
    }

    public static InsuredDependentBuilder getInsuredDependentBuilder(PlanId planId) {
        return new InsuredDependentBuilder(planId);
    }
}
