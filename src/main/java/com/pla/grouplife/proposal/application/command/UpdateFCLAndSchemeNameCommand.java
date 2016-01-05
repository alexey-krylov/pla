package com.pla.grouplife.proposal.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;

/**
 * Created by Admin on 05-Jan-16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFCLAndSchemeNameCommand {
    private String proposalId;
    private BigDecimal freeCoverLimit;
    private String schemeName;
    private UserDetails userDetails;

}
