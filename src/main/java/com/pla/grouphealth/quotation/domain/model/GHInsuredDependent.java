package com.pla.grouphealth.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class GHInsuredDependent {

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

    private String occupationClass;

    private String occupationCategory;

    private GHPlanPremiumDetail planPremiumDetail;

    private String existingIllness;

    private Integer minAgeEntry;

    private Integer maxAgeEntry;

    GHInsuredDependent(GHInsuredDependentBuilder insuredDependentBuilder) {
        checkArgument(insuredDependentBuilder != null);
        this.companyName = insuredDependentBuilder.getCompanyName();
        this.manNumber = insuredDependentBuilder.getManNumber();
        this.salutation = insuredDependentBuilder.getSalutation();
        this.nrcNumber = insuredDependentBuilder.getNrcNumber();
        this.firstName = insuredDependentBuilder.getFirstName();
        this.lastName = insuredDependentBuilder.getLastName();
        this.dateOfBirth = insuredDependentBuilder.getDateOfBirth();
        this.gender = insuredDependentBuilder.getGender();
        this.category = insuredDependentBuilder.getCategory();
        this.relationship = insuredDependentBuilder.getRelationship();
        this.planPremiumDetail = insuredDependentBuilder.getPlanPremiumDetail();
        if (isNotEmpty(insuredDependentBuilder.getCoveragePremiumDetails())) {
            this.planPremiumDetail.addAllCoveragePremiumDetail(insuredDependentBuilder.getCoveragePremiumDetails());
        }
        this.existingIllness = insuredDependentBuilder.getExistingIllness();
        this.minAgeEntry = insuredDependentBuilder.getMinAgeEntry();
        this.maxAgeEntry = insuredDependentBuilder.getMaxAgeEntry();
        this.occupationClass=insuredDependentBuilder.getOccupationClass();
    }

    public static GHInsuredDependentBuilder getInsuredDependentBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        return new GHInsuredDependentBuilder(planId, planCode, premiumAmount, sumAssured);
    }

    public GHInsuredDependent updatePlanPremiumAmount(BigDecimal insuredPlanProratePremium) {
        this.planPremiumDetail = this.planPremiumDetail.updatePremiumAmount(insuredPlanProratePremium);
        return this;
    }

    public BigDecimal getBasicAnnualVisibleCoveragePremiumForDependent() {
        BigDecimal basicAnnualVisibleCoveragePremiumOfDependent = BigDecimal.ZERO;
        GHPlanPremiumDetail planPremiumDetail = this.getPlanPremiumDetail();
        if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
            for (GHCoveragePremiumDetail coveragePremiumDetail : planPremiumDetail.getCoveragePremiumDetails()) {
                if ("YES".equals(coveragePremiumDetail.getPremiumVisibility())) {
                    basicAnnualVisibleCoveragePremiumOfDependent = basicAnnualVisibleCoveragePremiumOfDependent.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
                }
            }
        }
        return basicAnnualVisibleCoveragePremiumOfDependent;
    }

    public BigDecimal getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremiumForDependent() {
        BigDecimal basicAnnualPremiumOfDependent = BigDecimal.ZERO;
        GHPlanPremiumDetail planPremiumDetail = this.getPlanPremiumDetail();
        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getPremiumAmount() : BigDecimal.ZERO);
        if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
            for (GHCoveragePremiumDetail coveragePremiumDetail : planPremiumDetail.getCoveragePremiumDetails()) {
                if ("NO".equals(coveragePremiumDetail.getPremiumVisibility())) {
                    basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
                }
            }
        }
        return basicAnnualPremiumOfDependent;
    }

}
