package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.sharedkernel.domain.model.ClaimId;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by nthdimensioncompany on 15/12/2015.
 */
@Getter
@Setter
public class ClaimApproverCommentsDto {

   // private ObjectId id;

    private ClaimId claimId;

    private ClaimStatus claimStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;

    private String status;

}
