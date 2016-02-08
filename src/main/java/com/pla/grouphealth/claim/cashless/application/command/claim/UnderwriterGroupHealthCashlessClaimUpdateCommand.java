package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDto;
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
public class UnderwriterGroupHealthCashlessClaimUpdateCommand {
    private GroupHealthCashlessClaimDto groupHealthCashlessClaimDto;
    private String userName;
}
