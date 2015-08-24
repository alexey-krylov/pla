package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.Policy;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by Mirror on 8/19/2015.
 */

@Getter

public class ClaimantDetails {

    private ClaimType claimType;

    private Policy policy;

    private AssuredDetail assuredDetail;

    private DateTime claimIntimationDate;

    public ClaimantDetails(ClaimType claimType, Policy policy, AssuredDetail assuredDetail, DateTime claimIntimationDate) {
        this.claimIntimationDate = claimIntimationDate;
        this.claimType = claimType;
        this.policy = policy;
        this.assuredDetail = assuredDetail;
        
    }


}
