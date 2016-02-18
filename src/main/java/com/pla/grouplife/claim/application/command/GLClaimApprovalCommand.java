package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.presentation.dto.ClaimApproverCoverageDetailDto;
import com.pla.grouplife.claim.presentation.dto.ClaimApproverPlanDto;
import com.pla.grouplife.claim.presentation.dto.ClaimReviewDto;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ak
 */

@Getter
@Setter
public class GLClaimApprovalCommand {

    private String claimId;
    private ClaimApproverPlanDto claimApprovalPlanDetail;
    private List<ClaimApproverCoverageDetailDto> claimApprovalCoverageDetails;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredAmount;
    private List<ClaimReviewDto> reviewDetails;
    private DateTime referredToReassureOn;
    private DateTime responseReceivedOn;
    private String criteria;
    private UserDetails userDetails;
    private ClaimStatus status;
    private String comments;

}
