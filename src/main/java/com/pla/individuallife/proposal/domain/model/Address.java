package com.pla.individuallife.proposal.domain.model;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by pradyumna on 22-05-2015.
 */

@Getter
public class Address {
    private String address1;
    private String address2;
    private int postalCode;
    private String province;
    private String town;
    Address(String address1, String address2, int postalCode, String province, String town) {
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.province = province;
        this.town = town;
    }
}
