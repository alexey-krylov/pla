package com.pla.grouplife.sharedresource.model.vo;

import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
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
import java.util.List;
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

    @Setter
    private Set<InsuredDependent> insuredDependents;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private FamilyId familyId;

    private Relationship relationship;

    private Boolean isInsuredDeleted = Boolean.FALSE;

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

    public static InsuredBuilder getInsuredBuilder(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured, BigDecimal incomeMultiplier,List<ComputedPremiumDto> computedPremiumDtoList) {
        return new InsuredBuilder(planId, planCode, premiumAmount, sumAssured,incomeMultiplier,computedPremiumDtoList);
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
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPremiumForDependent(PremiumFrequency.ANNUALLY));
        return basicAnnualPremium;
    }

    public BigDecimal getBasicSemiAnnualPremium() {
        BigDecimal basicAnnualPremium = planPremiumDetail.getSemiAnnualPremium();
        if (isNotEmpty(coveragePremiumDetails)) {
            for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                basicAnnualPremium = basicAnnualPremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
            }
        }
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPremiumForDependent(PremiumFrequency.SEMI_ANNUALLY));
        return basicAnnualPremium;
    }

    public BigDecimal getBasicQuarterlyPremium() {
        BigDecimal basicAnnualPremium = planPremiumDetail.getQuarterlyPremium();
        if (isNotEmpty(coveragePremiumDetails)) {
            for (CoveragePremiumDetail coveragePremiumDetail :coveragePremiumDetails) {
                basicAnnualPremium = basicAnnualPremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
            }
        }
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPremiumForDependent(PremiumFrequency.QUARTERLY));
        return basicAnnualPremium;
    }

    public BigDecimal getBasicMonthlyPremium() {
        BigDecimal basicAnnualPremium = planPremiumDetail.getMonthlyPremium();
        if (isNotEmpty(coveragePremiumDetails)) {
            for (CoveragePremiumDetail coveragePremiumDetail :coveragePremiumDetails) {
                basicAnnualPremium = basicAnnualPremium.add(coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium() : BigDecimal.ZERO);
            }
        }
        basicAnnualPremium = basicAnnualPremium.add(getBasicAnnualPremiumForDependent(PremiumFrequency.MONTHLY));
        return basicAnnualPremium;
    }


    private BigDecimal getBasicAnnualPremiumForDependent(PremiumFrequency premiumFrequency) {
        BigDecimal basicAnnualPremiumOfDependent = BigDecimal.ZERO;
        if (isNotEmpty(this.insuredDependents)) {
            for (InsuredDependent insuredDependent : this.insuredDependents) {
                PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                switch (premiumFrequency){
                    case ANNUALLY:
                        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getPremiumAmount() : BigDecimal.ZERO);
                        break;
                    case SEMI_ANNUALLY:
                        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getSemiAnnualPremium() : BigDecimal.ZERO);
                        break;
                    case QUARTERLY:
                        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getQuarterlyPremium() : BigDecimal.ZERO);
                        break;
                    case MONTHLY:
                        basicAnnualPremiumOfDependent = basicAnnualPremiumOfDependent.add(planPremiumDetail != null ? planPremiumDetail.getMonthlyPremium() : BigDecimal.ZERO);
                        break;
                }
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

    public Insured updateWithDeletedMembers(List<String> deletedFamilyIds) {
        if (deletedFamilyIds.contains(this.familyId!=null?this.familyId.getFamilyId():"")){
            this.isInsuredDeleted = Boolean.TRUE;
        }
        for (InsuredDependent insuredDependent :this.insuredDependents){
            insuredDependent.updateWithDeletedMembers(deletedFamilyIds);
        }
        return this;
    }

}