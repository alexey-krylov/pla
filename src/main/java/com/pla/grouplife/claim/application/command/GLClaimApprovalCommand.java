package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.presentation.dto.ClaimReviewDto;
import com.pla.grouplife.claim.presentation.dto.CoverageDetailDto;
import com.pla.grouplife.claim.presentation.dto.PlanDetailDto;
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
    private PlanDetailDto planDetailDto;
    private List<CoverageDetailDto> coverageDetailDtos;
    private List<BigDecimal> approvedAmount;
    private List<BigDecimal> recoveredAmount;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredAmount;
    private List<ClaimReviewDto> reviewDetails;
    private DateTime referredToReassureOn;
    private DateTime responseReceivedOn;
    private UserDetails userDetails;
    private ClaimStatus status;
    private String comments;

}
