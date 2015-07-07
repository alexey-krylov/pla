package com.pla.grouphealth.proposal.application.command;

import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 7/7/2015.
 */
@Getter
@Setter
public class GHProposalApprovalCommand {

    private UserDetails userDetails;

    private ProposalStatus status;

    private String comment;

    private String proposalId;
}
