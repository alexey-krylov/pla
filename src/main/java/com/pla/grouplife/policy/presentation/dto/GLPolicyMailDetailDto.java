package com.pla.grouplife.policy.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Admin on 9/14/2015.
 */
@Getter
@Setter
public class GLPolicyMailDetailDto {


    private String proposerName;
    private String policyHolderName;
    private String address;
    private String telephoneNumber;
    private String inceptionDate;
    private String expiryDate;
    private String agentName;
    private String showLoading;
    private String agentBranch;
    private String agentCode;
    private String agentMobileNumber;
    private String masterPolicyNumber;
    private String totalSumAssured;
    private String totalLivesCovered;
    private List<CoverDetail> coverDetails;
    private List<Annexure> annexure;
    private String specialConditions;

    private String netPremium;
    private String profitAndSolvencyLoading;
    private String serviceTax;
    private String totalPremium;
    private String addOnBenefits;
    private String addOnBenefitsPercentage;
    private String waiverOfExcessLoadings;
    private String waiverOfExcessLoadingsPercentage;
    private String hivDiscount;
    private String valuedClientDiscount;
    private String longTermDiscount;

    private String issueBranch;
    private String issuanceDate;
    private String policyTerm;


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

        public Annexure(String insuredName, String nrc, String sex, String dob, String category, String status, String age, String annualIncome, String basicPremium) {
            this.insuredName = insuredName;
            this.nrc = nrc;
            this.sex = sex;
            this.dob = dob;
            this.category = category;
            this.status = status;
            this.age = age;
            this.annualIncome = annualIncome;
            this.basicPremium = basicPremium;
        }
    }

}
