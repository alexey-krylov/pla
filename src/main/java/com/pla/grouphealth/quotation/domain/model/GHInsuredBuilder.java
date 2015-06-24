package com.pla.grouphealth.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@Getter
public class GHInsuredBuilder {

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private Set<GHInsuredDependent> insuredDependents;

    private GHPlanPremiumDetail planPremiumDetail;

    private String occupation;

    private List<GHCoveragePremiumDetail> coveragePremiumDetails;

    private String existingIllness;

    private Integer minAgeEntry;

    private Integer maxAgeEntry;

    private Integer noOfAssured;


    GHInsuredBuilder(PlanId insuredPlan, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        checkArgument(insuredPlan != null);
        checkArgument(isNotEmpty(planCode));
        checkArgument(premiumAmount != null);
        GHPlanPremiumDetail planPremiumDetail = new GHPlanPremiumDetail(insuredPlan, planCode, premiumAmount, sumAssured);
        this.planPremiumDetail = planPremiumDetail;
    }

    public GHInsuredBuilder withInsuredName(String salutation, String firstName, String lastName) {
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    public GHInsuredBuilder withInsuredNrcNumber(String nrcNumber) {
        this.nrcNumber = nrcNumber;
        return this;
    }

    public GHInsuredBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public GHInsuredBuilder withManNumber(String manNumber) {
        this.manNumber = manNumber;
        return this;
    }

    public GHInsuredBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public GHInsuredBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public GHInsuredBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public GHInsuredBuilder withDependents(Set<GHInsuredDependent> insuredDependents) {
        this.insuredDependents = insuredDependents;
        return this;
    }

    public GHInsuredBuilder withOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }


    public GHInsuredBuilder withNoOfAssured(Integer noOfAssured) {
        this.noOfAssured = noOfAssured;
        return this;
    }

    public GHInsuredBuilder withCoveragePremiumDetail(GHCoveragePremiumDetailBuilder ghCoveragePremiumDetailBuilder) {
        GHCoveragePremiumDetail coveragePremiumDetail = new GHCoveragePremiumDetail(ghCoveragePremiumDetailBuilder.getCoverageName(), ghCoveragePremiumDetailBuilder.getCoverageCode(), new CoverageId(ghCoveragePremiumDetailBuilder.getCoverageId()), ghCoveragePremiumDetailBuilder.getPremium(), ghCoveragePremiumDetailBuilder.getPremiumVisibility(), ghCoveragePremiumDetailBuilder.getSumAssured());
        coveragePremiumDetail = coveragePremiumDetail.addAllBenefitLimit(ghCoveragePremiumDetailBuilder.getBenefitPremiumLimits());
        if (isEmpty(this.coveragePremiumDetails)) {
            this.coveragePremiumDetails = new ArrayList<>();
        }
        this.coveragePremiumDetails.add(coveragePremiumDetail);
        return this;
    }

    public GHInsuredBuilder withMinAndMaxAge(Integer minAgeEntry, Integer maxAgeEntry) {
        this.minAgeEntry = minAgeEntry;
        this.maxAgeEntry = maxAgeEntry;
        return this;
    }

    public GHInsuredBuilder withExistingIllness(String existingIllness) {
        this.existingIllness = existingIllness;
        return this;
    }

    public GHInsured build() {
        return new GHInsured(this);
    }

}
