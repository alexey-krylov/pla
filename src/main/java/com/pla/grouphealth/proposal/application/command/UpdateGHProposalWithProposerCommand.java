package com.pla.grouphealth.proposal.application.command;

import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 7/6/2015.
 */
@Getter
@Setter
public class UpdateGHProposalWithProposerCommand {

    private ProposerDto proposerDto;

    private String proposalId;

    private UserDetails userDetails;
}
