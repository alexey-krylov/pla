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

    private GLClaimApproverPlanDetail planDetail;
    private List<ApproverCoverageDetail> coverageDetails;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredAmount;
    private BigDecimal additionalAmountPaid;
    private List<ClaimReviewDetail> reviewDetails;
    private String comment;
    private DateTime referredToReassuredOn;
    private DateTime responseReceivedOn;

    public GlClaimUnderWriterApprovalDetail(GLClaimApproverPlanDetail planDetail,List<ApproverCoverageDetail> coverageDetails,BigDecimal totalApprovedAmount,
                                            String comment,DateTime referredToReassuredOn,DateTime responseReceivedOn){
        this.planDetail=planDetail;
        this.coverageDetails=coverageDetails;
        this.totalApprovedAmount=totalApprovedAmount;
        this.comment=comment;
        this.referredToReassuredOn=referredToReassuredOn;
        this.responseReceivedOn=responseReceivedOn;
    }
    public GlClaimUnderWriterApprovalDetail withRecoveredAmountDetails( BigDecimal totalRecoveredAmount){
        this.totalRecoveredAmount=totalRecoveredAmount;
        return this;
    }
    public GlClaimUnderWriterApprovalDetail withAdditionalAmountPaidDetails( BigDecimal amount){
        this.additionalAmountPaid=amount;
        return this;
    }
    public GlClaimUnderWriterApprovalDetail withClaimReviewDetails(List<ClaimReviewDetail> reviewDetails){
      this.reviewDetails=reviewDetails;
        return this;
    }

}

