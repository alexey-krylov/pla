package com.pla.grouplife.quotation.application.command;

import com.pla.grouplife.sharedresource.dto.ProposerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateGLQuotationWithProposerCommand {

    private ProposerDto proposerDto;

    private String quotationId;

    private UserDetails userDetails;
}
