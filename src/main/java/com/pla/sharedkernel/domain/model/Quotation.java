package com.pla.sharedkernel.domain.model;

import com.pla.sharedkernel.identifier.QuotationId;
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

    private QuotationId quotationId;

    public Quotation(String quotationNumber, int versionNumber,QuotationId quotationId) {
        this.quotationNumber = quotationNumber;
        this.versionNumber = versionNumber;
        this.quotationId=quotationId;
    }
}
