package com.pla.grouplife.claim.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by nthdimensioncompany on 16/12/2015.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GLClaimDocumentRemoveCommand {
    private UserDetails userDetails;
    private String claimId;
    private String gridFsDocId;
}
