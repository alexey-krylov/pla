package com.pla.grouplife.sharedresource.model.vo;

import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.PremiumType;
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
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class Insured {

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

    private BigDecimal oldAnnualIncome;

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private PremiumType premiumType;

    private String rateOfPremium;

    private Set<InsuredDependent> insuredDependents;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private FamilyId familyId;

    private Relationship relationship;

    Insured(InsuredBuilder insuredBuilder) {
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
        this.annualIncome = insuredBuilder.getAnnualIncome();
        this.oldAnnualIncome = insuredBuilder.getOldAnnualIncome();
        this.occupationClass = insuredBuilder.getOccupation();
        this.coveragePremiumDetails = insuredBuilder.getCoveragePremiumDetails();
        this.noOfAssured = insuredBuilder.getNoOfAssured();
        this.premiumType = insuredBuilder.getPremiumType();
        this.rateOfPremium = insuredBuilder.getRateOfPremium();
        if (isNotEmpty(insuredBuilder.getFamilyId())) {
            this.familyId = new FamilyId(insuredBuilder.getFamilyId());
        }
    }

    public static InsuredBuilder getInsuredBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured, BigDecimal incomeMultiplier) {
        return new InsuredBuilder(planId, planCode, premiumAmount, sumAssured,incomeMultiplier );
    }

    public Insured updatePlanPremiumAmount(BigDecimal premiumAmount) {
        this.planPremiumDetail = this.planPremiumDetail.updatePremiumAmount(premiumAmount);
        return this;
    }

    public BigDecimal getBasicAnnualPremium() {
        BigDecimal basicAnnualPremium = planPremiumDetail!=null?planPremiumDetail.getPlanId()!=null?planPremiumDetail.getPremiumAmount():BigDecimal.ZERO: BigDecimal.ZERO;
        if (isNotEmpty(coveragePremiumDetails)) {
            for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                basicAnnualPremium = basicAnnualPremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
            }
        }
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPremiumForDependent());
        return basicAnnualPremium;
    }

    private BigDecimal getBasicAnnualPremiumForDependent() {
        BigDecimal basicAnnualPremiumOfDependent = BigDecimal.ZERO;
        if (isNotEmpty(this.insuredDependents)) {
            for (InsuredDependent insuredDependent : this.insuredDependents) {
                PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getPremiumAmount(): BigDecimal.ZERO);
                if (isNotEmpty(insuredDependent.getCoveragePremiumDetails())) {
                    for (CoveragePremiumDetail coveragePremiumDetail : insuredDependent.getCoveragePremiumDetails()) {
                        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
                    }
                }
            }
        }
        return basicAnnualPremiumOfDependent;
    }

    public Insured updateWithFamilyId(FamilyId familyId) {
        this.familyId = familyId;
        return this;
    }
}