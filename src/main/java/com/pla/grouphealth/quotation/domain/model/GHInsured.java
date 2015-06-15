package com.pla.grouphealth.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class GHInsured {

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private Set<GHInsuredDependent> insuredDependents;

    private GHPlanPremiumDetail planPremiumDetail;

    private String existingIllness;

    private Integer minAgeEntry;

    private Integer maxAgeEntry;

    GHInsured(GHInsuredBuilder insuredBuilder) {
        checkArgument(insuredBuilder != null);
        this.planPremiumDetail = insuredBuilder.getPlanPremiumDetail();
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
        this.occupationClass = insuredBuilder.getOccupation();
        if (isNotEmpty(insuredBuilder.getCoveragePremiumDetails())) {
            this.planPremiumDetail.addAllCoveragePremiumDetail(insuredBuilder.getCoveragePremiumDetails());
        }
        this.existingIllness = insuredBuilder.getExistingIllness();
        this.minAgeEntry = insuredBuilder.getMinAgeEntry();
        this.maxAgeEntry = insuredBuilder.getMaxAgeEntry();
        this.noOfAssured = insuredBuilder.getNoOfAssured();

    }

    public static GHInsuredBuilder getInsuredBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        return new GHInsuredBuilder(planId, planCode, premiumAmount, sumAssured);
    }

    public GHInsured updatePlanPremiumAmount(BigDecimal premiumAmount) {
        this.planPremiumDetail = this.planPremiumDetail.updatePremiumAmount(premiumAmount);
        return this;
    }

    public BigDecimal getBasicAnnualPremium() {
        BigDecimal basicAnnualPremium = planPremiumDetail.getPremiumAmount();
        if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
            for (GHCoveragePremiumDetail coveragePremiumDetail : planPremiumDetail.getCoveragePremiumDetails()) {
                basicAnnualPremium = basicAnnualPremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
            }
        }
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPremiumForDependent());
        return basicAnnualPremium;
    }

    public BigDecimal getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremium() {
        BigDecimal basicAnnualPremium = planPremiumDetail.getPremiumAmount();
        if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
            for (GHCoveragePremiumDetail coveragePremiumDetail : planPremiumDetail.getCoveragePremiumDetails()) {
                if ("NO".equals(coveragePremiumDetail.getPremiumVisibility())) {
                    basicAnnualPremium = basicAnnualPremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
                }
            }
        }
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremiumForDependent());
        return basicAnnualPremium;
    }

    public BigDecimal getInsuredBasicAnnualVisibleCoveragePremium() {
        BigDecimal totalVisibleCoveragePremium = BigDecimal.ZERO;
        if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
            for (GHCoveragePremiumDetail coveragePremiumDetail : planPremiumDetail.getCoveragePremiumDetails()) {
                if ("YES".equals(coveragePremiumDetail.getPremiumVisibility())) {
                    totalVisibleCoveragePremium = totalVisibleCoveragePremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
                }
            }
        }
        totalVisibleCoveragePremium = totalVisibleCoveragePremium.add(getBasicAnnualVisibleCoveragePremiumForDependent());
        return totalVisibleCoveragePremium;
    }

    private BigDecimal getBasicAnnualPremiumForDependent() {
        BigDecimal basicAnnualPremiumOfDependent = BigDecimal.ZERO;
        if (isNotEmpty(this.insuredDependents)) {
            for (GHInsuredDependent insuredDependent : this.insuredDependents) {
                GHPlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getPremiumAmount() : BigDecimal.ZERO);
                if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
                    for (GHCoveragePremiumDetail coveragePremiumDetail : planPremiumDetail.getCoveragePremiumDetails()) {
                        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
                    }
                }
            }
        }
        return basicAnnualPremiumOfDependent;
    }

    private BigDecimal getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremiumForDependent() {
        BigDecimal basicAnnualPremiumOfDependent = BigDecimal.ZERO;
        if (isNotEmpty(this.insuredDependents)) {
            for (GHInsuredDependent insuredDependent : this.insuredDependents) {
                basicAnnualPremiumOfDependent = insuredDependent.getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremiumForDependent();
            }
        }
        return basicAnnualPremiumOfDependent;
    }

    private BigDecimal getBasicAnnualVisibleCoveragePremiumForDependent() {
        BigDecimal basicAnnualVisibleCoveragePremiumOfDependent = BigDecimal.ZERO;
        if (isNotEmpty(this.insuredDependents)) {
            for (GHInsuredDependent insuredDependent : this.insuredDependents) {
                basicAnnualVisibleCoveragePremiumOfDependent = basicAnnualVisibleCoveragePremiumOfDependent.add(insuredDependent.getBasicAnnualVisibleCoveragePremiumForDependent());
            }
        }
        return basicAnnualVisibleCoveragePremiumOfDependent;
    }
}
