package com.pla.grouplife.claim.domain.model;

import org.joda.time.DateTime;

/**
 * Created by ak
 */
public class ClaimReopenProcessor {

    private String userName;
    public ClaimReopenProcessor(String userName) {
        this.userName = userName;
    }

    public GroupLifeClaim submitForReopen(DateTime approvalOn, String comment,GroupLifeClaim groupLifeClaim) {
        groupLifeClaim = groupLifeClaim.markAsReopenClaim(this.userName, approvalOn, comment);
        return groupLifeClaim;
    }
}
