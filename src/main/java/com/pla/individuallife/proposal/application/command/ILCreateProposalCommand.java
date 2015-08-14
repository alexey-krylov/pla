package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.dto.ProposerDto;
import com.pla.individuallife.sharedresource.model.vo.ProposalPlanDetail;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by Prasant on 26-May-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILCreateProposalCommand {
    private ProposedAssuredDto proposedAssured;
    private ProposerDto proposer;
    private ProposalPlanDetail planDetail;
    private UserDetails userDetails;
    private Set<AgentDetailDto> agentCommissionDetails;
    private String proposalId;
    private String quotationId;
}
