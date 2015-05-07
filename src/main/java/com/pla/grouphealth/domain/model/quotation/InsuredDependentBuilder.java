package com.pla.grouphealth.domain.model.quotation;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
public class InsuredDependentBuilder {

    private PlanId insuredPlan;

    private Set<CoverageId> insuredCoverages;

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private Set<InsuredDependent> insuredDependents;

    private Set<Policy> policies;

    InsuredDependentBuilder(PlanId insuredPlan) {
        checkArgument(insuredPlan != null);
        this.insuredPlan = insuredPlan;
    }

    public InsuredDependentBuilder withCoverageIds(Set<CoverageId> insuredCoverages) {
        this.insuredCoverages = insuredCoverages;
        return this;
    }

    public InsuredDependentBuilder withInsuredName(String salutation, String firstName, String lastName) {
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    public InsuredDependentBuilder withInsuredNrcNumber(String nrcNumber) {
        this.nrcNumber = nrcNumber;
        return this;
    }

    public InsuredDependentBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public InsuredDependentBuilder withManNumber(String manNumber) {
        this.manNumber = manNumber;
        return this;
    }

    public InsuredDependentBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public InsuredDependentBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public InsuredDependentBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public InsuredDependentBuilder withDependents(Set<InsuredDependent> insuredDependents) {
        this.insuredDependents = insuredDependents;
        return this;
    }

    public InsuredDependentBuilder withPremiums(Set<Policy> policies) {
        this.policies = policies;
        return this;
    }

    public InsuredDependent build() {
        return new InsuredDependent(this);
    }
}
