package com.pla.grouphealth.domain.event.quotation;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;

/**
 * Created by Karunakar on 4/30/2015.
 */
@EqualsAndHashCode
public class QuotationClosedEvent {

    private QuotationId quotationId;

    public QuotationClosedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
