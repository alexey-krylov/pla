package com.pla.grouplife.claim.domain.event;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.sharedkernel.domain.model.ClaimId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by ak on 10/12/2015.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class GLClaimStatusAuditEvent {

    private ClaimId claimId;

    private ClaimStatus status;

    private String actor;

    private String comments;

    private DateTime performedOn;
}
