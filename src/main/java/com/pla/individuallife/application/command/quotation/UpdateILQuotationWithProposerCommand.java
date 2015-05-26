package com.pla.individuallife.application.command.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.presentation.dto.ProposerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 5/15/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateILQuotationWithProposerCommand {

    private ProposerDto proposerDto;

    private AgentId agentId;

    private String quotationId;

    private UserDetails userDetails;
}
