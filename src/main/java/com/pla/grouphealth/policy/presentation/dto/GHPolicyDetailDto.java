package com.pla.grouphealth.policy.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Admin on 9/20/2015.
 */
@Getter
@Setter
public class GHPolicyDetailDto {

    private String agentName;
    private String agentBranch;
    private String agentCode;
    private String agentMobileNumber;

    private String proposerName;
    private String address;
    private String telephoneNumber;
    private String totalLivesCovered;
    private String inceptionDate;
    private String expiryDate;
    private String policyTerm;
    private String issueBranch;
    private String issuanceDate;
    private String masterPolicyNumber;
    private List<CoverDetail> coverDetails;
    private List<Annexure> annexure;
    private String showLoading;
    private String specialConditions;

    /*
    *
    * For Policy Document Detail
    * */
    private String planName;
    private String netPremium;
    private String totalSumAssured;
    private String profitAndSolvencyLoading;
    private String additionalDiscountLoading;
    private String serviceTax;
    private String totalPremium;
    private String addOnBenefits;
    private String addOnBenefitsPercentage;
    private String waiverOfExcessLoadings;
    private String waiverOfExcessLoadingsPercentage;

    /*
    * For Debit/Credit Detail
    * */
    private String commissionAmount;
    private String endorsementNumber;
    private String endorsementEffectiveDate;
    private String endorsementDetailHeaderName;
    private String premiumPaymentEndDate;

    @Getter
    @Setter
    @EqualsAndHashCode(of = {"category", "relationship", "planCoverageName"})
    public class CoverDetail {
        private String category;
        private String relationship;
        private String planCoverageName;
        private String planCoverageSumAssured;
        private BigDecimal sumAssured;

        public CoverDetail(String category, String relationship, String planCoverageName, BigDecimal sumAssured) {
            this.category = category;
            this.relationship = relationship;
            this.planCoverageName = planCoverageName;
            this.sumAssured = sumAssured;
        }

        public CoverDetail(String category, String relationship, String planCoverageName) {
            this.category = category;
            this.relationship = relationship;
            this.planCoverageName = planCoverageName;
        }

        public CoverDetail addSumAssured(String sumAssured) {
            this.planCoverageSumAssured = sumAssured;
            return this;
        }
    }

    @Getter
    @Setter
    public class Annexure {
        private String insuredName;
        private String nrc;
        private String sex;
        private String dob;
        private String category;
        private String status;
        private String age;
        private String annualIncome;
        private String basicPremium;
        private String planPremium;
        private String visibleCoveragePremium;


        public Annexure(String insuredName, String nrc, String sex, String dob, String category, String status, String age, String annualIncome, String basicPremium, String planPremium, String visibleCoveragePremium) {
            this.insuredName = insuredName;
            this.nrc = nrc;
            this.sex = sex;
            this.dob = dob;
            this.category = category;
            this.status = status;
            this.age = age;
            this.annualIncome = annualIncome;
            this.basicPremium = basicPremium;
            this.planPremium = planPremium;
            this.visibleCoveragePremium = visibleCoveragePremium;
        }
    }
}

