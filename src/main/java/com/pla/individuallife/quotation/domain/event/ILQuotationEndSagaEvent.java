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
public class ILQuotationEndSagaEvent {

    private QuotationId quotationId;

    public ILQuotationEndSagaEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
