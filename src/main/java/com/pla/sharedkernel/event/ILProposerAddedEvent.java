package com.pla.sharedkernel.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Admin on 9/29/2015.
 */
@Getter
@EqualsAndHashCode
public class ILProposerAddedEvent {

    private String proposerName;

    private String proposerCode;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    public ILProposerAddedEvent(String proposerName, String proposerCode, String addressLine1, String addressLine2, String postalCode, String province, String town, String emailAddress) {
        this.proposerName = proposerName;
        this.proposerCode = proposerCode;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postalCode = postalCode;
        this.province = province;
        this.town = town;
        this.emailAddress = emailAddress;
    }
}
