package com.pla.grouplife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;

/**
 * Created by Samir on 4/8/2015.
 */
@EqualsAndHashCode
public class GLQuotationClosedEvent {

    private QuotationId quotationId;

    public GLQuotationClosedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
