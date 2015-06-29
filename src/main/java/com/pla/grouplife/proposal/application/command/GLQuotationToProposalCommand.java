package com.pla.grouplife.proposal.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 5/31/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GLQuotationToProposalCommand {

    private String quotationId;

    private UserDetails userDetails;
}
