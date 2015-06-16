package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;

/**
 * Created by pradyumna on 16-06-2015.
 */
public class ILQuotationPurgeEvent {
    private QuotationId quotationId;

    public ILQuotationPurgeEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
