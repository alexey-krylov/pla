package com.pla.grouplife.claim.domain.model;

import lombok.*;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ak on 11/1/2016.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
@Setter(value =AccessLevel.PACKAGE )

public class GlClaimUnderWriterApprovalDetail {

    private PlanDetail planDetail;
    private List<CoverageDetail> coverageDetails;
    private List<BigDecimal> approvedAmount;
    private List<BigDecimal> recoveredAmount;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredAmount;
    private List<ClaimReviewDetail> reviewDetails;
    private String comment;
    private DateTime referredToReassuredOn;
    private DateTime responseReceivedOn;

    public GlClaimUnderWriterApprovalDetail(PlanDetail planDetail,List<CoverageDetail> coverageDetails,List<BigDecimal> approvedAmount,BigDecimal totalApprovedAmount,
                                            String comment,DateTime referredToReassuredOn,DateTime responseReceivedOn){
        this.planDetail=planDetail;
        this.coverageDetails=coverageDetails;
        this.approvedAmount=approvedAmount;
        this.totalApprovedAmount=totalApprovedAmount;
        this.comment=comment;
        this.referredToReassuredOn=referredToReassuredOn;
        this.responseReceivedOn=responseReceivedOn;
    }
    public GlClaimUnderWriterApprovalDetail withRecoveredAmountDetails( List<BigDecimal> recoveredAmount,BigDecimal totalRecoveredAmount){
        this.recoveredAmount=recoveredAmount;
        this.totalRecoveredAmount=totalRecoveredAmount;
        return this;
    }
    public GlClaimUnderWriterApprovalDetail withClaimReviewDetails(List<ClaimReviewDetail> reviewDetails){
      this.reviewDetails=reviewDetails;
        return this;
    }

}

