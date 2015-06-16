package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;

/**
 * Created by pradyumna on 16-06-2015.
 */
public class ILQuotationReminderEvent {
    private QuotationId quotationId;

    public ILQuotationReminderEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
