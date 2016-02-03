package com.pla.grouplife.claim.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;

/**
 * Created by ak on 23/12/2015.
 */
@Getter
@Setter
public class GLClaimAmendmentCommand {
    String claimId;
    private BigDecimal amountRecovered;
    private String remarks;
    private UserDetails userDetails;
    private String comment;

}
