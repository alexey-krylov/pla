package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Document(collection = "GROUP_HEALTH_CASHLESS_CLAIM")
@NoArgsConstructor
@Getter
public class GHCashlessClaim {
    private GHCashlessClaimId gHCashlessClaimId;
}
