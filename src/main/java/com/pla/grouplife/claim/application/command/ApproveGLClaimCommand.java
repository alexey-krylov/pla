package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by ak on 11/12/2015.
 */

@Getter
@Setter
public class ApproveGLClaimCommand {

    private String claimId;
    private UserDetails userDetails;
    private ClaimStatus status;
    private String comment;

}
