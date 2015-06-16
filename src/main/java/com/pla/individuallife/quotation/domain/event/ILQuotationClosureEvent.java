package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;

/**
 * Created by pradyumna on 16-06-2015.
 */
public class ILQuotationClosureEvent {

    private QuotationId quotationId;

    public ILQuotationClosureEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
