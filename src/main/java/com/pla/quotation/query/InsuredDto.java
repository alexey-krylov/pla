package com.pla.quotation.query;

import com.pla.sharedkernel.domain.model.Gender;
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

/**
 * Created by Samir on 4/29/2015.
 */
@Getter
@Setter
public class InsuredDto {

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

    private Set<InsuredDependentDto> insuredDependents;

    private PlanPremiumDetailDto planPremiumDetail;

    private List<CoveragePremiumDetailDto> coveragePremiumDetails;


    public InsuredDto addInsuredDependent(Set<InsuredDependentDto> insuredDependentDtos){
        this.insuredDependents=insuredDependentDtos;
        return this;
    }


    public InsuredDto addPlanPremiumDetail(PlanPremiumDetailDto planPremiumDetailDto) {
        this.planPremiumDetail = planPremiumDetailDto;
        return this;
    }

    public InsuredDto addCoveragePremiumDetails(List<CoveragePremiumDetailDto> coveragePremiumDetailDtos) {
        this.coveragePremiumDetails = coveragePremiumDetailDtos;
        return this;
    }

    @Getter
    @Setter
    public static class InsuredDependentDto {

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

        private PlanPremiumDetailDto planPremiumDetail;

        private List<CoveragePremiumDetailDto> coveragePremiumDetails;

        public InsuredDependentDto addPlanPremiumDetail(PlanPremiumDetailDto planPremiumDetailDto) {
            this.planPremiumDetail = planPremiumDetailDto;
            return this;
        }

        public InsuredDependentDto addCoveragePremiumDetails(List<CoveragePremiumDetailDto> coveragePremiumDetailDtos) {
            this.coveragePremiumDetails = coveragePremiumDetailDtos;
            return this;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanPremiumDetailDto {

        private String planId;

        private String planCode;

        private BigDecimal premiumAmount;

        private BigDecimal incomeMultiplier;

        private BigDecimal sumAssured;

        public PlanPremiumDetailDto(String planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
            this.planId = planId;
            this.planCode = planCode;
            this.premiumAmount = premiumAmount;
            this.sumAssured = sumAssured;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CoveragePremiumDetailDto {

        private String coverageCode;

        private String coverageId;

        private BigDecimal premium;

        private String coverageName;

        private BigDecimal sumAssured;


        public CoveragePremiumDetailDto(String coverageCode, String coverageId, BigDecimal premium, BigDecimal sumAssured) {
            this.coverageCode = coverageCode;
            this.coverageId = coverageId;
            this.premium = premium;
            this.sumAssured = sumAssured;
        }



    }
}
