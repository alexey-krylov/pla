package com.pla.grouplife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.axonframework.event.ISagaEvent;

/**
 * Created by Samir on 6/18/2015.
 */
@EqualsAndHashCode
@Getter
@ToString
public class GLQuotationEndSagaEvent implements ISagaEvent {

    private QuotationId quotationId;

    public GLQuotationEndSagaEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
