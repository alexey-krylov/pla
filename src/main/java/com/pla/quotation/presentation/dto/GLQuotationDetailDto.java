package com.pla.quotation.presentation.dto;

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
    private String proposerName;
    private String proposerAddress;
    private String proposerPhoneNumber;
    private String agentCode;
    private String quotationNumber;
    private String coveragePeriod;
    private String totalLivesCovered;
    private String totalSumAssured;
    private String planName;
    private String agentMobileNumber;
    private String netPremium;
    private String profitAndSolvencyLoading;
    private String additionalDiscountLoading;
    private String serviceTax;
    private String totalPremium;
    private String specialConditions;
    private String addOnBenefits;
    private String addOnBenefitsPercentage;
    private String waiverOfExcessLoadings;
    private String waiverOfExcessLoadingsPercentage;
    private List<CoverDetail> coverDetails;
    private List<Annexure> annexure;


    public class CoverDetail {

    }

    public class Annexure {

    }
}
