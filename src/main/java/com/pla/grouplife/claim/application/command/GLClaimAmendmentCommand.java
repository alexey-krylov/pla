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
 * Created by ak on 23/12/2015.
 */
@Getter
@Setter
public class GLClaimAmendmentCommand {
    private String claimId;
    private ClaimApproverPlanDto planDetail;
    private List<ClaimApproverCoverageDetailDto> coverageDetails;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredAmount;
    private List<ClaimReviewDto> reviewDetails;
    private DateTime referredToReassureOn;
    private DateTime responseReceivedOn;
    private UserDetails userDetails;
    private ClaimStatus status;
    private String comments;

}
