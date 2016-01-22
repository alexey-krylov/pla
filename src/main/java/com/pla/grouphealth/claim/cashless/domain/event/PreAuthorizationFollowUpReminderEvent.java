package com.pla.grouphealth.claim.cashless.domain.event;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import lombok.*;

import java.io.Serializable;

/**
 * Created by Mohan Sharma on 1/13/2016.
 */
@Getter
@ToString
public class PreAuthorizationFollowUpReminderEvent implements Serializable{

    private static final long serialVersionUID = 4401616796204536261L;
    private String preAuthorizationRequestId;

    public PreAuthorizationFollowUpReminderEvent(String preAuthorizationRequestId){
        this.preAuthorizationRequestId = preAuthorizationRequestId;
    }
}
