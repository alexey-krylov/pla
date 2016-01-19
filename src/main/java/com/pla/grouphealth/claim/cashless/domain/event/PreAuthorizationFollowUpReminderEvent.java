package com.pla.grouphealth.claim.cashless.domain.event;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by Mohan Sharma on 1/13/2016.
 */
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PreAuthorizationFollowUpReminderEvent implements Serializable{
    private PreAuthorizationRequestId preAuthorizationRequestId;
}
