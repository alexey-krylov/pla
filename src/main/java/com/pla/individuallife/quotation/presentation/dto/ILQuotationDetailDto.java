package com.pla.individuallife.quotation.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Karunakar on 6/9/2015.
 */
@Getter
@Setter
public class ILQuotationDetailDto {

    private String agentName;
    private String agentBranch;
    private String agentSalutation;
    private String agentCode;
    private String agentMobileNumber;

    private String proposerName;
    private String proposerEmailAddress;
    private String proposerMobileNumber;

    private String quotationNumber;
    private String proposedCoverPeriod;

    private String proposedAssuredName;
    private String proposedAssuredDob;
    private String proposedAssuredMobileNumber;

    private String netAnnualPremium;
    private String netSemiAnnualPremium;
    private String netQuarterlyPremium;
    private String netMonthlyPremium;

    private String specialConditions="";


    private List<CoverDetail> coverDetails;



    @Getter
    @Setter
    public class CoverDetail {
        private String sumAssured;
        private Integer coverTerm;
        private String planOrCoverageName;

        public CoverDetail(String planOrCoverageName, String sumAssured, Integer coverTerm ) {
            this.planOrCoverageName = planOrCoverageName;
            this.sumAssured = sumAssured;
            this.coverTerm = coverTerm;
        }
    }

}
