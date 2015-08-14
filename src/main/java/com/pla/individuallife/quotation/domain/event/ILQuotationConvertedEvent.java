package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Admin on 7/28/2015.
 */
@EqualsAndHashCode
@Getter
@ToString
public class ILQuotationConvertedEvent {
    private QuotationId quotationId;

    public ILQuotationConvertedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
