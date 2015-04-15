package com.pla.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;

/**
 * Created by Samir on 4/8/2015.
 */
public class QuotationClosedEvent {

    private QuotationId quotationId;

    public QuotationClosedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
