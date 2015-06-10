package com.pla.grouphealth.quotation.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Samir on 5/25/2015.
 */
@Getter
@Setter
public class GLQuotationDetailDto {

    private String agentName;
    private String agentBranch;
    private String agentSalutation;
    private String agentCode;
    private String agentMobileNumber;

    private String proposerName;
    private String proposerAddress;

    private String proposerPhoneNumber;
    private String quotationNumber;
    private String coveragePeriod;
    private String totalLivesCovered;

    private String planName;
    private String netPremium;
    private String totalSumAssured;
    private String profitAndSolvencyLoading;
    private String additionalDiscountLoading;
    private String serviceTax;
    private String totalPremium;
    private String specialConditions = "";
    private String addOnBenefits;
    private String addOnBenefitsPercentage;
    private String waiverOfExcessLoadings;
    private String waiverOfExcessLoadingsPercentage;


    private List<CoverDetail> coverDetails;
    private List<Annexure> annexure;


    @Getter
    @Setter
    public class CoverDetail {
        private String category;
        private String relationship;
        private String planCoverageName;
        private String planCoverageSumAssured;

        public CoverDetail(String category, String relationship, String planCoverageName, String planCoverageSumAssured) {
            this.category = category;
            this.relationship = relationship;
            this.planCoverageName = planCoverageName;
            this.planCoverageSumAssured = planCoverageSumAssured;
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
