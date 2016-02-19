package com.pla.grouplife.claim.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by ak on 18/2/2016.
 */
@Setter
@Getter

public class GLClaimReopenCommand {

    private String claimId;

    private UserDetails userDetails;

    private String comment;
}
