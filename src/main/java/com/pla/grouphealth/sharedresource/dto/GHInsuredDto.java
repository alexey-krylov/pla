package com.pla.grouphealth.sharedresource.dto;

import com.google.common.collect.Lists;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.PremiumType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 4/29/2015.
 */
@Getter
@Setter
public class GHInsuredDto {

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

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private PremiumType premiumType;

    private String existingIllness;

    private Integer minAgeEntry;

    private Integer maxAgeEntry;

    private Set<GHInsuredDependentDto> insuredDependents;

    private GHPlanPremiumDetailDto planPremiumDetail;

    private List<GHCoveragePremiumDetailDto> coveragePremiumDetails;


    public GHInsuredDto addInsuredDependent(Set<GHInsuredDependentDto> insuredDependentDtos) {
        this.insuredDependents = insuredDependentDtos;
        return this;
    }


    public GHInsuredDto addPlanPremiumDetail(GHPlanPremiumDetailDto planPremiumDetailDto) {
        this.planPremiumDetail = planPremiumDetailDto;
        return this;
    }

    public GHInsuredDto addCoveragePremiumDetails(List<GHCoveragePremiumDetailDto> coveragePremiumDetailDtos) {
        this.coveragePremiumDetails = coveragePremiumDetailDtos;
        return this;
    }

    @Getter
    @Setter
    public static class GHInsuredDependentDto {

        private PlanId insuredDependentPlan;

        private Set<CoverageId> insuredDependentCoverages;

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

        private GHPlanPremiumDetailDto planPremiumDetail;

        private String existingIllness;

        private Integer minAgeEntry;

        private Integer maxAgeEntry;

        private List<GHCoveragePremiumDetailDto> coveragePremiumDetails;

        private Integer noOfAssured;

        private PremiumType premiumType;

        public GHInsuredDependentDto addPlanPremiumDetail(GHPlanPremiumDetailDto planPremiumDetailDto) {
            this.planPremiumDetail = planPremiumDetailDto;
            return this;
        }

        public GHInsuredDependentDto addCoveragePremiumDetails(List<GHCoveragePremiumDetailDto> coveragePremiumDetailDtos) {
            this.coveragePremiumDetails = coveragePremiumDetailDtos;
            return this;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GHPlanPremiumDetailDto {

        private String planId;

        private String planCode;

        private BigDecimal premiumAmount;

        private BigDecimal incomeMultiplier;

        private BigDecimal sumAssured;

        public GHPlanPremiumDetailDto(String planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
            this.planId = planId;
            this.planCode = planCode;
            this.premiumAmount = premiumAmount;
            this.sumAssured = sumAssured;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GHCoveragePremiumDetailDto {

        private String coverageCode;

        private String coverageId;

        private BigDecimal premium;

        private String coverageName;

        private BigDecimal sumAssured;

        private String premiumVisibility;

        private List<GHCoverageBenefitDetailDto> benefitDetails;

        public GHCoveragePremiumDetailDto(String coverageCode, String coverageId, BigDecimal premium, BigDecimal sumAssured,String premiumVisibility) {
            this.coverageCode = coverageCode;
            this.coverageId = coverageId;
            this.premium = premium;
            this.sumAssured = sumAssured;
            this.premiumVisibility=premiumVisibility;
        }

        public GHCoveragePremiumDetailDto addBenefit(GHCoverageBenefitDetailDto benefitDetail) {
            if (isEmpty(this.benefitDetails)) {
                this.benefitDetails = Lists.newArrayList();
            }
            this.benefitDetails.add(benefitDetail);
            return this;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class GHCoverageBenefitDetailDto {

            private String benefitCode;

            private String benefitId;

            private BigDecimal benefitLimit;

        }

    }
}
