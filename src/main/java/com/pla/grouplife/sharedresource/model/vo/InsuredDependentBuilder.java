package com.pla.grouplife.sharedresource.model.vo;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
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
 * Created by Samir on 4/8/2015.
 */
@Getter
public class InsuredDependentBuilder {

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String occupationClass;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private Relationship relationship;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private Integer noOfAssured;

    InsuredDependentBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        checkArgument(planId != null);
        checkArgument(isNotEmpty(planCode));
        checkArgument(premiumAmount != null);
        PlanPremiumDetail planPremiumDetail = new PlanPremiumDetail(planId, planCode, premiumAmount, sumAssured);
        this.planPremiumDetail = planPremiumDetail;
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

    public InsuredDependentBuilder withOccupationClass(String occupation) {
        this.occupationClass = occupation;
        return this;
    }

    public InsuredDependentBuilder withRelationship(Relationship relationship) {
        this.relationship = relationship;
        return this;
    }

    public InsuredDependentBuilder withNoOfAssured(Integer noOfAssured) {
        this.noOfAssured = noOfAssured;
        return this;
    }

    public InsuredDependentBuilder withCoveragePremiumDetail(String coverageName, String coverageCode, String coverageId, BigDecimal premium,BigDecimal sumAssured) {
        CoveragePremiumDetail coveragePremiumDetail = new CoveragePremiumDetail(coverageName, coverageCode, new CoverageId(coverageId), premium,sumAssured);
        if (isEmpty(this.coveragePremiumDetails)) {
            this.coveragePremiumDetails = new HashSet<>();
        }
        this.coveragePremiumDetails.add(coveragePremiumDetail);
        return this;
    }

    public InsuredDependent build() {
        return new InsuredDependent(this);
    }
}
