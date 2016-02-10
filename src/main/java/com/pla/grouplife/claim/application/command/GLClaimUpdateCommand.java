package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.presentation.dto.ClaimDisabilityRegistrationDto;
import com.pla.grouplife.claim.presentation.dto.ClaimRegistrationDto;
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
    private ClaimRegistrationDto incidentDetails;
    private ClaimDisabilityRegistrationDto disabilityIncidentDetails;
    private Set<GLClaimDocumentCommand> uploadedDocuments;
    private UserDetails userDetails;
}
