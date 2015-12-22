package com.pla.grouplife.claim.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by ak
 */
@Setter
@Getter
public class SubmitGLClaimCommand {

    private String claimId;

    private UserDetails userDetails;

    private String comment;
}
