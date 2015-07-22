package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;

/**
 * Created by Prasant on 27-May-15.
 */
@Getter
public class ResidentialAddress {

    private Address address;
    private Long homePhone;
    private String emailAddress;

    public ResidentialAddress(Address address, Long homePhone,String emailAddress) {
        this.address = address;
        this.homePhone = homePhone;
        this.emailAddress=emailAddress;
    }
}