package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.ILProposalStatus;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 7/23/2015.
 */
@Getter
@Setter
public class ILProposalUnderwriterNextLevelCommand {

    private UserDetails userDetails;

    private ILProposalStatus status;

    private String comment;

    private String proposalId;

    private RoutingLevel routingLevel;

}
