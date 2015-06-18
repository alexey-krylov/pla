package com.pla.grouplife.proposal.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GenerateGLProposalCommand {

    private String quotationId;

    private UserDetails userDetails;
}
