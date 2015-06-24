package com.pla.grouphealth.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/8/2015.
 */
@Getter
public class GHInsuredDependentBuilder {

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

    private GHPlanPremiumDetail planPremiumDetail;

    private List<GHCoveragePremiumDetail> coveragePremiumDetails;

    private String existingIllness;

    private Integer minAgeEntry;

    private Integer maxAgeEntry;

    private String occupationClass;

    private Integer noOfAssured;


    GHInsuredDependentBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        checkArgument(planId != null);
        checkArgument(isNotEmpty(planCode));
        checkArgument(premiumAmount != null);
        GHPlanPremiumDetail planPremiumDetail = new GHPlanPremiumDetail(planId, planCode, premiumAmount, sumAssured);
        this.planPremiumDetail = planPremiumDetail;
    }

    public GHInsuredDependentBuilder withInsuredName(String salutation, String firstName, String lastName) {
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    public GHInsuredDependentBuilder withInsuredNrcNumber(String nrcNumber) {
        this.nrcNumber = nrcNumber;
        return this;
    }

    public GHInsuredDependentBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public GHInsuredDependentBuilder withManNumber(String manNumber) {
        this.manNumber = manNumber;
        return this;
    }

    public GHInsuredDependentBuilder withNoOfAssured(Integer noOfAssured) {
        this.noOfAssured = noOfAssured;
        return this;
    }


    public GHInsuredDependentBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public GHInsuredDependentBuilder withOccupationClass(String occupationClass) {
        this.occupationClass = occupationClass;
        return this;
    }

    public GHInsuredDependentBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public GHInsuredDependentBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public GHInsuredDependentBuilder withRelationship(Relationship relationship) {
        this.relationship = relationship;
        return this;
    }

    public GHInsuredDependentBuilder withCoveragePremiumDetail(GHCoveragePremiumDetailBuilder ghCoveragePremiumDetailBuilder) {
        GHCoveragePremiumDetail coveragePremiumDetail = new GHCoveragePremiumDetail(ghCoveragePremiumDetailBuilder.getCoverageName(), ghCoveragePremiumDetailBuilder.getCoverageCode(), new CoverageId(ghCoveragePremiumDetailBuilder.getCoverageId()), ghCoveragePremiumDetailBuilder.getPremium(), ghCoveragePremiumDetailBuilder.getPremiumVisibility(), ghCoveragePremiumDetailBuilder.getSumAssured());
        coveragePremiumDetail = coveragePremiumDetail.addAllBenefitLimit(ghCoveragePremiumDetailBuilder.getBenefitPremiumLimits());
        if (isEmpty(this.coveragePremiumDetails)) {
            this.coveragePremiumDetails = new ArrayList<>();
        }
        this.coveragePremiumDetails.add(coveragePremiumDetail);
        return this;
    }

    public GHInsuredDependentBuilder withMinAndMaxAge(Integer minAgeEntry, Integer maxAgeEntry) {
        this.minAgeEntry = minAgeEntry;
        this.maxAgeEntry = maxAgeEntry;
        return this;
    }

    public GHInsuredDependentBuilder withExistingIllness(String existingIllness) {
        this.existingIllness = existingIllness;
        return this;
    }

    public GHInsuredDependent build() {
        return new GHInsuredDependent(this);
    }
}
