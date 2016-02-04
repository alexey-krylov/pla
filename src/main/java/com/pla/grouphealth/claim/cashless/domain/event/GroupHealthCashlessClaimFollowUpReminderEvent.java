package com.pla.grouphealth.claim.cashless.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Mohan Sharma on 1/13/2016.
 */
@NoArgsConstructor
@Getter
@ToString
public class GroupHealthCashlessClaimFollowUpReminderEvent implements Serializable{

    private static final long serialVersionUID = 4401616796204536261L;
    private String groupHealthCashlessClaimId;

    public GroupHealthCashlessClaimFollowUpReminderEvent(String groupHealthCashlessClaimId){
        this.groupHealthCashlessClaimId = groupHealthCashlessClaimId;
    }
}
