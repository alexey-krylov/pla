package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.presentation.dto.ClaimDisabilityRegistrationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by ak
 */
@Getter
@Setter
@NoArgsConstructor
public class GLDisabilityClaimRegistrationCommand {

    private String claimId;
    private ClaimDisabilityRegistrationDto disabilityIncidentDetails;
    private UserDetails userDetails;
    private String comments;
    private Set<GLClaimDocumentCommand> uploadedDocuments;
}
