package com.pla.grouplife.claim.domain.model;

import com.pla.grouplife.claim.presentation.dto.ApprovalDetailsDto;
import com.pla.grouplife.claim.presentation.dto.ClaimReviewDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by ak on 21/12/2015.
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)

public class GLClaimAmendment {
    private ApprovalDetailsDto approvalDetailsDto;
    private ClaimReviewDto claimReviewDto;
    private DateTime referredToReAssurerOn;
    private DateTime responseReceivedOn;
}
