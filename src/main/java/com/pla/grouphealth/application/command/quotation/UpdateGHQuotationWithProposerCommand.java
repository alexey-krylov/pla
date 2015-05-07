package com.pla.grouphealth.application.command.quotation;

import com.pla.grouphealth.query.ProposerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateGHQuotationWithProposerCommand {

    private ProposerDto proposerDto;

    private String quotationId;

    private UserDetails userDetails;
}
