package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 9/22/2015.
 */
@Getter
public class ClaimNumber {

    private String claimNumber;

    public ClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;

    }

}
