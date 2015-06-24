package com.pla.individuallife.proposal.application.command;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.proposal.domain.model.ProposalPlanDetail;
import com.pla.individuallife.proposal.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.proposal.presentation.dto.ProposerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Map;

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
    private Map<AgentId, BigDecimal> agentCommissionDetails;

    private String proposalId;

}
