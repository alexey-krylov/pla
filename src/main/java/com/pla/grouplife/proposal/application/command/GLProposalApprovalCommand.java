package com.pla.grouplife.proposal.application.command;

import com.pla.grouplife.sharedresource.model.vo.GLProposalStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@Setter
public class GLProposalApprovalCommand {

    private UserDetails userDetails;

    private GLProposalStatus status;

    private String comment;

    private String proposalId;
}
