package com.pla.grouplife.claim.domain.model;

import org.joda.time.DateTime;

/**
 * Created by ak
 */
public class ClaimAmendmentProcessor {



 private String userName;

    public ClaimAmendmentProcessor(String userName) {
        this.userName = userName;
    }

    public GroupLifeClaim submitForClaimAmendment(DateTime approvalOn, String comment,GroupLifeClaim groupLifeClaim) {
        groupLifeClaim = groupLifeClaim.markAsAmendedClaim(this.userName,approvalOn,comment);
        return groupLifeClaim;
    }

}
