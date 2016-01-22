package com.pla.grouphealth.claim.cashless.domain.event;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Mohan Sharma on 1/14/2016.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class PreAuthorizationClosureEvent implements Serializable{
    private static final long serialVersionUID = 4401616796204536261L;
    private String preAuthorizationRequestId;
}
