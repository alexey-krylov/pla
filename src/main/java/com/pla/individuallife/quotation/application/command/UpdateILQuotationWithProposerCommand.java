package com.pla.individuallife.quotation.application.command;

import com.pla.individuallife.quotation.presentation.dto.ProposerDto;
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

    private String agentId;

    private String quotationId;

    private UserDetails userDetails;
}
