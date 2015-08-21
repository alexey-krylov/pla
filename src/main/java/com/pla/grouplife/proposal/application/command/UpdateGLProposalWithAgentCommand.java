package com.pla.grouplife.proposal.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateGLProposalWithAgentCommand {

    @NotNull(message = "{Agent ID cannot be null}")
    @NotEmpty(message = "{Agent ID cannot be empty}")
    private String agentId;

    private String proposalId;

    private UserDetails userDetails;
}
