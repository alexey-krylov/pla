package com.pla.grouplife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@Getter
public class InsuredBuilder {

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

    private Set<InsuredDependent> insuredDependents;

    private PlanPremiumDetail planPremiumDetail;

    private String occupation;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    InsuredBuilder(PlanId insuredPlan, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        checkArgument(insuredPlan != null);
        checkArgument(isNotEmpty(planCode));
        checkArgument(premiumAmount != null);
        PlanPremiumDetail planPremiumDetail = new PlanPremiumDetail(insuredPlan, planCode, premiumAmount, sumAssured);
        this.planPremiumDetail = planPremiumDetail;
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

    public InsuredBuilder withAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
        return this;
    }

    public InsuredBuilder withOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public InsuredBuilder withCoveragePremiumDetail(String coverageName, String coverageCode, String coverageId, BigDecimal premium) {
        CoveragePremiumDetail coveragePremiumDetail = new CoveragePremiumDetail(coverageName, coverageCode, new CoverageId(coverageId), premium);
        if (isEmpty(this.coveragePremiumDetails)) {
            this.coveragePremiumDetails = new HashSet<>();
        }
        this.coveragePremiumDetails.add(coveragePremiumDetail);
        return this;
    }

    public Insured build() {
        return new Insured(this);
    }
}
