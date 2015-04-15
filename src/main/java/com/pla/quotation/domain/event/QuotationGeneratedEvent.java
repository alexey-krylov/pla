package com.pla.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;

/**
 * Created by Samir on 4/8/2015.
 */
public class QuotationGeneratedEvent {

    private QuotationId quotationId;

    public QuotationGeneratedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
