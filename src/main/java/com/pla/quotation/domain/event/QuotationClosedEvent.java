package com.pla.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

/**
 * Created by Samir on 4/8/2015.
 */
@Getter
public class QuotationClosedEvent {

    private QuotationId quotationId;

    public QuotationClosedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
