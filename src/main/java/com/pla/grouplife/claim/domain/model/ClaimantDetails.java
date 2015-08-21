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

    private String assuredIdField;

    private String assuredIdNumber;

    private String assuredFirstName;

    private String assuredSurName;

    private DateTime assuredDob;

    private DateTime claimIntimationDate;


}
