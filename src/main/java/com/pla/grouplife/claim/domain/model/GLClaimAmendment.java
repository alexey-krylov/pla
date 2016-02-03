package com.pla.grouplife.claim.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ak on 21/12/2015.
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)

public class GLClaimAmendment {

    private  PlanDetail planDetail;
    private List<CoverageDetail> coverageDetails;
    private List<BigDecimal> approvedAmount;
    private List<BigDecimal> recoveredAmount;
    private BigDecimal totalApprovedAmount;
    private BigDecimal totalRecoveredAmountAmount;
    private List<ClaimReviewDetail> reviewDetails;
    private String comment;
    private DateTime referredToReassuredOn;
    private DateTime responseReceivedOn;
}
