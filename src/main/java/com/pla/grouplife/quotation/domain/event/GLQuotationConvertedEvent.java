package com.pla.grouplife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.axonframework.event.ISagaEvent;

/**
 * Created by Samir on 4/8/2015.
 */
@EqualsAndHashCode
@Getter
@ToString
public class GLQuotationConvertedEvent implements ISagaEvent{

    private QuotationId quotationId;

    public GLQuotationConvertedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
