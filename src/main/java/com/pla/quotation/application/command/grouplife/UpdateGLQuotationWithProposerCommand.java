package com.pla.quotation.application.command.grouplife;

import com.pla.quotation.query.ProposerDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
public class UpdateGLQuotationWithProposerCommand {

    private ProposerDto proposerDto;

    private String quotationId;

    private UserDetails userDetails;
}
