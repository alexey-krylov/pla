package com.pla.grouplife.claim.domain.event;

import com.pla.sharedkernel.domain.model.ClaimId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Created by ak on 11/12/2015.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter

public class GLClaimSubmitEvent implements Serializable {

    private ClaimId claimId;
}
