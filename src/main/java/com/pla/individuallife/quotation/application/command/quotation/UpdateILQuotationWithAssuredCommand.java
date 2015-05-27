package com.pla.individuallife.quotation.application.command.quotation;

import com.pla.individuallife.quotation.presentation.dto.ProposedAssuredDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 5/20/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateILQuotationWithAssuredCommand {

    private ProposedAssuredDto proposedAssuredDto;

    private Boolean isAssuredTheProposer;

    private String quotationId;

    private UserDetails userDetails;
}
