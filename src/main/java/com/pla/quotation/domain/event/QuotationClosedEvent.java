package com.pla.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;

/**
 * Created by Samir on 4/8/2015.
 */
@EqualsAndHashCode
public class QuotationClosedEvent {

    private QuotationId quotationId;

    public QuotationClosedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
