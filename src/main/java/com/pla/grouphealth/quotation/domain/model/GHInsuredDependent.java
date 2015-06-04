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
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

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

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

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
        this.coveragePremiumDetails = insuredDependentBuilder.getCoveragePremiumDetails();
    }

    public static GHInsuredDependentBuilder getInsuredDependentBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        return new GHInsuredDependentBuilder(planId, planCode, premiumAmount, sumAssured);
    }

    public GHInsuredDependent updatePlanPremiumAmount(BigDecimal insuredPlanProratePremium) {
        this.planPremiumDetail = this.planPremiumDetail.updatePremiumAmount(insuredPlanProratePremium);
        return this;
    }
}
