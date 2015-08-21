package com.pla.individuallife.proposal.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 7/16/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class SubmitILProposalCommand {

    private String proposalId;

    private UserDetails userDetails;

    private String comment;
}
