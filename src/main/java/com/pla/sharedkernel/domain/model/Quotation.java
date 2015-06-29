package com.pla.sharedkernel.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Samir on 6/25/2015.
 */
@EqualsAndHashCode
@Getter
public class Quotation {

    private String quotationNumber;

    private int versionNumber;

    public Quotation(String quotationNumber, int versionNumber) {
        this.quotationNumber = quotationNumber;
        this.versionNumber = versionNumber;
    }
}
