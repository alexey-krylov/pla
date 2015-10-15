package com.pla.sharedkernel.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Admin on 9/29/2015.
 */
@Getter
@EqualsAndHashCode
public class ILProposerAddedEvent {

    private String clientId;

    private String proposerName;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    public ILProposerAddedEvent(String clientId,String proposerName, String addressLine1, String addressLine2, String postalCode, String province, String town, String emailAddress) {
        this.clientId = clientId;
        this.proposerName = proposerName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postalCode = postalCode;
        this.province = province;
        this.town = town;
        this.emailAddress = emailAddress;
    }
}
