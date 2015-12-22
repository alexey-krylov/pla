package com.pla.grouplife.claim.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by ak on 10/12/2015.
 */

@Getter
@Setter
@NoArgsConstructor
public class GLClaimUpdateCommand {

    private String claimId;
    private String claimNumber;
    private GLDisabilityClaimRegistrationCommand disableCommand;
    private GLClaimRegistrationCommand claimCommand;
    private Set<GLClaimDocumentCommand> uploadedDocuments;
    private UserDetails userDetails;
}
