package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.presentation.dto.ClaimRegistrationDto;
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
public class GLClaimRegistrationCommand {
    private String claimId;
    private ClaimRegistrationDto incidentDetails;
    private UserDetails userDetails;
    private String comments;
    private Set<GLClaimDocumentCommand> uploadedDocuments;
}

