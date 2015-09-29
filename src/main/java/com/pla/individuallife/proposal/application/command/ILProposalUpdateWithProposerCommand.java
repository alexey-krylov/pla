package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.dto.ProposerDto;
import com.pla.individuallife.sharedresource.model.vo.ProposalPlanDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by Karunakar on 6/24/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateWithProposerCommand {
    private ProposalPlanDetail planDetail;
    private ProposerDto proposer;
    private Set<AgentDetailDto> agentCommissionDetails;
    private UserDetails userDetails;
    private String proposalId;
}
