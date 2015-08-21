package com.pla.individuallife.sharedresource.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Karunakar on 6/25/2015.
 */
@EqualsAndHashCode
@Getter
public class Quotation {

    private String quotationNumber;

    private int versionNumber;

    private String quotationId;

    public Quotation(String quotationNumber, int versionNumber,String quotationId) {
        this.quotationNumber = quotationNumber;
        this.versionNumber = versionNumber;
        this.quotationId=quotationId;
    }
}
