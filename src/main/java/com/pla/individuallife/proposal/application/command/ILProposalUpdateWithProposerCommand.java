package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 6/24/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateWithProposerCommand {

    private ProposedAssuredDto proposer;
    private UserDetails userDetails;
    private String proposalId;
}
