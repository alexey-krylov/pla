package com.pla.grouphealth.claim.cashless.application.command.preauthorization;

import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.PreAuthorizationClaimantDetailCommand;
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
public class AddRequirementPreAuthorizationCommand {
    private PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand;
    private String userName;
}
