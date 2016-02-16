package com.pla.grouplife.claim.domain.model;

import org.joda.time.DateTime;

/**
 * Created by ak
 */

public class GLClaimSettlementProcessor {

    private String userName;

    public GLClaimSettlementProcessor(String userName) {
        this.userName = userName;
    }

    public GroupLifeClaim submitForClaimSettlement(DateTime approvalOn, String comment,GroupLifeClaim groupLifeClaim) {
        groupLifeClaim = groupLifeClaim.markAsSettledClaim(this.userName,approvalOn,comment);
        return groupLifeClaim;
    }

}
