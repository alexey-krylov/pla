package com.pla.quotation.domain.model.grouplife;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Samir on 4/7/2015.
 */
@Getter
public class InsuredBuilder {

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

    InsuredBuilder(PlanId insuredPlan) {
        checkArgument(insuredPlan != null);
        this.insuredPlan = insuredPlan;
    }

    public InsuredBuilder withCoverageIds(Set<CoverageId> insuredCoverages) {
        this.insuredCoverages = insuredCoverages;
        return this;
    }

    public InsuredBuilder withInsuredName(String salutation, String firstName, String lastName) {
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    public InsuredBuilder withInsuredNrcNumber(String nrcNumber) {
        this.nrcNumber = nrcNumber;
        return this;
    }

    public InsuredBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public InsuredBuilder withManNumber(String manNumber) {
        this.manNumber = manNumber;
        return this;
    }

    public InsuredBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public InsuredBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public InsuredBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public InsuredBuilder withDependents(Set<InsuredDependent> insuredDependents) {
        this.insuredDependents = insuredDependents;
        return this;
    }

    public InsuredBuilder withPremiums(Set<Policy> policies) {
        this.policies = policies;
        return this;
    }

    public Insured build() {
        return new Insured(this);
    }
}
