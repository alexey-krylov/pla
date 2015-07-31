package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Getter
public class ILQuotationClosureEvent implements Serializable {

    private static final long serialVersionUID = 4337137184061763117L;
    private QuotationId quotationId;

    public ILQuotationClosureEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
