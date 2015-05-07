package com.pla.grouphealth.domain.event.quotation;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
public class QuotationGeneratedEvent {

    private QuotationId quotationId;

    public QuotationGeneratedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
