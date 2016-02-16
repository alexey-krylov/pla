package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.presentation.dto.GLClaimSettlementDataDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class GLClaimSettlementCommand {

    private String claimId;
    private GLClaimSettlementDataDto claimSettlementDetails;
    private UserDetails userDetails;
    private String comment;
    private ClaimStatus claimStatus;
}
