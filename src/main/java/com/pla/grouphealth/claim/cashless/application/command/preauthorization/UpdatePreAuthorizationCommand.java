package com.pla.grouphealth.claim.cashless.application.command.preauthorization;

import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.PreAuthorizationClaimantDetailCommand;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author - Mohan Sharma Created on 1/27/2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdatePreAuthorizationCommand {
    private PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand;
    private RoutingLevel routingLevel;
    private String userName;
}
