package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author - Mohan Sharma Created on 1/28/2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UnderwriterPreAuthorizationUpdateCommand {
    private PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand;
    private String userName;
}
