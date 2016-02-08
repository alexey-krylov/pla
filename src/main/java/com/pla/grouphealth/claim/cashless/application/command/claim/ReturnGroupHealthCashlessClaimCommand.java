package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 1/25/2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReturnGroupHealthCashlessClaimCommand {
    private GroupHealthCashlessClaimDto groupHealthCashlessClaimDto;
    private String userName;
}
