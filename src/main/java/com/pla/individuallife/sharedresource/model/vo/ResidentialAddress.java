package com.pla.individuallife.sharedresource.model.vo;

import lombok.Getter;

/**
 * Created by Prasant on 27-May-15.
 */
@Getter
public class ResidentialAddress {

    private Address address;
    private String homePhone;
    private String emailAddress;

    public ResidentialAddress(Address address, String homePhone,String emailAddress) {
        this.address = address;
        this.homePhone = homePhone;
        this.emailAddress=emailAddress;
    }
}