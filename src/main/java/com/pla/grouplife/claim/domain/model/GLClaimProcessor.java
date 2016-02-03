package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.PolicyId;
import org.joda.time.DateTime;

/**
 * Created by ak
 *
 */
public class GLClaimProcessor {

    private String userName;

    public GLClaimProcessor(String userName) {
        this.userName = userName;
    }

        public GroupLifeClaim submitClaimIntimation(DateTime approvalOn,  GroupLifeClaim groupLifeClaim) {
            groupLifeClaim = groupLifeClaim.submitForClaimIntimationCreation(approvalOn,this.userName);
            return groupLifeClaim;
        }


    public GroupLifeClaim createClaim(String claimIdInString, String claimNumberInString, String policyId, String policyNumber, String policyHolderName, ClaimType claimType) {
       ClaimId claimId = new ClaimId(claimIdInString);
       ClaimNumber claimNumber = new ClaimNumber(claimNumberInString);
        Policy policy = new Policy(new PolicyId(policyId), new PolicyNumber(policyNumber), policyHolderName);
        GroupLifeClaim groupLifeClaim = new  GroupLifeClaim(claimId, claimNumber, policy, claimType);
        return groupLifeClaim;
    }
}

