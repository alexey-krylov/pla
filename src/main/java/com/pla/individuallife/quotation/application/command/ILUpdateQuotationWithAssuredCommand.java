package com.pla.individuallife.quotation.application.command;

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
public class ILUpdateQuotationWithAssuredCommand {

    private ProposedAssuredDto proposedAssured;

    private boolean assuredTheProposer;

    private String quotationId;

    private UserDetails userDetails;
}
