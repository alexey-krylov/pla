package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDto;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author - Mohan Sharma Created on 2/11/2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GroupHealthCashlessClaimServiceMismatchedApprovedCommand {
    private GroupHealthCashlessClaimDto groupHealthCashlessClaimDto;
    private RoutingLevel routingLevel;
    private String userName;
}
