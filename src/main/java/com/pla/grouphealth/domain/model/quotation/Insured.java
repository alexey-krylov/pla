package com.pla.grouphealth.domain.model.quotation;

import com.pla.sharedkernel.domain.model.Gender;
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
public class Insured {

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

    private Set<Policy> selfPolices;


    Insured(InsuredBuilder insuredBuilder) {
        checkArgument(insuredBuilder != null);
        checkArgument(isNotEmpty(insuredBuilder.getPolicies()));
        this.insuredPlan = insuredBuilder.getInsuredPlan();
        this.insuredCoverages = insuredBuilder.getInsuredCoverages();
        this.companyName = insuredBuilder.getCompanyName();
        this.manNumber = insuredBuilder.getManNumber();
        this.salutation = insuredBuilder.getSalutation();
        this.nrcNumber = insuredBuilder.getNrcNumber();
        this.firstName = insuredBuilder.getFirstName();
        this.lastName = insuredBuilder.getLastName();
        this.dateOfBirth = insuredBuilder.getDateOfBirth();
        this.gender = insuredBuilder.getGender();
        this.category = insuredBuilder.getCategory();
        this.insuredDependents = insuredBuilder.getInsuredDependents();
        this.selfPolices = insuredBuilder.getPolicies();

    }

    public static InsuredBuilder getInsuredBuilder(PlanId planId) {
        return new InsuredBuilder(planId);
    }
}
