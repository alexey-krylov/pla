package com.pla.individuallife.proposal.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 6/18/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILConvertToProposalCommand {

    private String quotationId;

    private UserDetails userDetails;

}
