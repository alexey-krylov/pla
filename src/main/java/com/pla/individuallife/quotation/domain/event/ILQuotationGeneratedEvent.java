package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Getter
public class ILQuotationGeneratedEvent implements Serializable {

    private static final long serialVersionUID = -1024890197988461850L;
    private QuotationId quotationId;
    private String quotationARId;

    public ILQuotationGeneratedEvent(String quotationARId, QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
