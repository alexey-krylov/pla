package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ak on 21/12/2015.
 */

@Getter
@Setter
@NoArgsConstructor

public class ApprovalDetailsDto {

    private ClaimApproverPlanDto planDetails;
    private List<ClaimApproverCoverageDetailDto> coverageDetails;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredOrAdditionalAmount;
    private List<ClaimReviewDto> reviewDetails;
    private String comments;
    private DateTime referredToReassuredOn;
    private DateTime responseReceivedOn;

}
