package com.pla.grouphealth.claim.cashless.domain.event;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Mohan Sharma on 1/14/2016.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PreAuthorizationClosureEvent {
    private String preAuthorizationRequestId;
}
