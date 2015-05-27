package com.pla.grouplife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

/**
 * Created by Samir on 4/8/2015.
 */
@Getter
public class QuotationGeneratedEvent {

    private QuotationId quotationId;

    public QuotationGeneratedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
